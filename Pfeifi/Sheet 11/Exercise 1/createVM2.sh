#!/bin/bash
# enter config information here
#############################################################################
awsAccessKeyID=''
awsSecretAccessKey=''
region=''
keyName=''
amiID=''
secGroupID=''
#############################################################################

#aws local config
aws configure set AWS_ACCESS_KEY_ID $awsAccessKeyID
aws configure set AWS_SECRET_ACCESS_KEY $awsSecretAccessKey
aws configure set default.region $region

# get instance id from new instance-launch-request
id=$(aws ec2 run-instances --image-id $amiID --count 1 --instance-type t2.micro --key-name $keyName --security-group-ids $secGroupID --query Instances[0].InstanceId)
# remove these chars ""
id=${id//\"}
echo Starting instance with id: $id

# add id to a text file and dont display the response
mkdir ids > /dev/null 2>&1
# create new dir for this instance
foldername=$(echo -n $id | sha1sum)
foldername=$(echo "$foldername" | tr -cd '[[:alnum:]]')
mkdir $foldername
echo $id > "$foldername"/id.txt
printf '%s\n' $id >> ids/ids.txt

# create name for id
aws ec2 create-tags --resources $id --tags Key=Name,Value=$foldername

startTime=$(date +%s%N)
# get status from instance-id: as long as its not running: request new status
while true; do
  status=$(aws ec2 describe-instances --instance-ids $id --query Reservations[0].Instances[0].State.Name)
   echo "Status: "$status
   if [ $status = "\"running\"" ]; 
   then
    break
  fi
  sleep 2
done


# get the publicDnsName from new status to establish a connection
dnsName=$(aws ec2 describe-instances --instance-ids $id --query Reservations[0].Instances[0].PublicDnsName)
dnsName=${dnsName//\"}
 
# upload scripts to vm
scp -i $keyName.pem -o StrictHostKeyChecking=no copyFromS3ToVM2.sh ec2-user@$dnsName:~