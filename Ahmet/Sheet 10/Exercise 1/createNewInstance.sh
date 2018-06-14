#!/bin/bash
#enter config information here
#############################################################################
awsAccesKeyID=XXXXXXXXXXXX
awsSecretAccessKey=XXXXXXXXXXXX
region=XXXXXXXXXXXX
keyName=XXXXXXXXXXXX
amiID=XXXXXXXXXXXX
secGroupID=XXXXXXXXXXXX
#############################################################################

#aws local config
aws configure set AWS_ACCESS_KEY_ID $awsAccesKeyID
aws configure set AWS_SECRET_ACCESS_KEY $awsSecretAccessKey
aws configure set default.region $region

#get instance id from new instance-launch-request
id=$(aws ec2 run-instances --image-id $amiID --count 1 --instance-type t2.micro --key-name $keyName --security-group-ids $secGroupID --query Instances[0].InstanceId)
#remove these chars ""
id=${id//\"}
echo Starting instance with id: $id

#add id to a text file and dont display the response
mkdir ids > /dev/null 2>&1
#create new dir for this instance
foldername=$(echo -n $id | sha1sum)
foldername=$(echo "$foldername" | tr -cd '[[:alnum:]]')
mkdir $foldername
echo $id > "$foldername"/id.txt
printf '%s\n' $id >> ids/ids.txt

#create name for id
aws ec2 create-tags --resources $id --tags Key=Name,Value=$foldername

startTime=$(date +%s%N)
#get status from instance-id: as long as its not running: request new status
while true; do
  status=$(aws ec2 describe-instances --instance-ids $id --query Reservations[0].Instances[0].State.Name)
   echo "Status: "$status
   if [ $status = "\"running\"" ]; 
   then
    break
  fi
  sleep 2
done

#get the publicDnsName from new status to establish a connection
dnsName=$(aws ec2 describe-instances --instance-ids $id --query Reservations[0].Instances[0].PublicDnsName)
dnsName=${dnsName//\"}

#try to transfer a file to the server
while true; do
scp -i $keyName.pem -o StrictHostKeyChecking=no Hello.cpp ec2-user@$dnsName:~
	#exitstatus 1 -> something went wrong -> keep trying
	if [ "$?" = "1" ];
	then
		echo "Filecopy failed! Trying again..."
	else
		break
	fi
done

#the script to be executed with the ssh-req
script="sudo yum update -y;sudo yum install gcc-c++ -y; g++ Hello.cpp -o Hello; ./Hello"
echo "Connecting via SSH..."
#try connecting via ssh until success
while true; do
#before connecting measure time, if connecting fails count this period also to the startup
endTime=$(date +%s%N)	
totalTime=$(expr $endTime - $startTime)
totalTime=$(expr $totalTime / 1000000)
#write result to file
echo It took $totalTime ms to start the instance. > $foldername/start_time.txt
	#connect via ssh with script-exec instruction once connected
	ssh -i "$keyName.pem" -o StrictHostKeyChecking=no ec2-user@$dnsName $script
	if [ "$?" = "255" ];
	then
		echo "Connection refused! Trying again..."
	else
		break
	fi
done
