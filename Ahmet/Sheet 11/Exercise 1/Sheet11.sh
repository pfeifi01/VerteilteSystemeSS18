#!/bin/bash
#enter config information here
#############################################################################
awsAccesKeyID=XXXXXXX
awsSecretAccessKey=XXXXXX
region=XXXXX
#without .pem
keyName=XXXXXX
amiID=ami-XXXXXXXX
secGroupID=XXXXXXXX
scriptName=${0##*/}
yourBucketName=csat1763-bucket
#############################################################################

#aws local config
aws configure set AWS_ACCESS_KEY_ID $awsAccesKeyID
aws configure set AWS_SECRET_ACCESS_KEY $awsSecretAccessKey
aws configure set default.region $region

script1="
dd if=/dev/zero of=1.dat  bs=1K count=1;
dd if=/dev/zero of=2.dat  bs=10K  count=1;
dd if=/dev/zero of=3.dat  bs=100K  count=1;
dd if=/dev/zero of=4.dat  bs=1M  count=1;
dd if=/dev/zero of=5.dat  bs=10M  count=1;
aws configure set AWS_ACCESS_KEY_ID $awsAccesKeyID;
aws configure set AWS_SECRET_ACCESS_KEY $awsSecretAccessKey;
chmod 400 $keyName.pem;
chmod +x $scriptName;
./$scriptName vm1ts3;
./$scriptName simpleCreate;
echo VM-1 done;
"

script2="
mkdir fromVM > /dev/null 2>&1;
mkdir fromBucket > /dev/null 2>&1;
aws configure set AWS_ACCESS_KEY_ID $awsAccesKeyID;
aws configure set AWS_SECRET_ACCESS_KEY $awsSecretAccessKey;
./$scriptName s3tvm2;
echo VM-2 done;
"

startNewInstance()
{

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
	mkdir $foldername > /dev/null 2>&1
	echo $id > "$foldername"/id.txt
	printf '%s\n' $id >> ids/ids.txt

	#create name for id
	aws ec2 create-tags --resources $id --tags Key=Name,Value=$foldername

	#get status from instance-id: as long as its not running: request new status
	while true; do
		status=$(aws ec2 describe-instances --instance-ids $id --query Reservations[0].Instances[0].State.Name)
		echo "Status: "$status
		if [ $status = "\"running\"" ]; 
		then
			break
		else
			sleep 2
		fi

	done

	#get the publicDnsName from new status to establish a connection
	dnsName=$(aws ec2 describe-instances --instance-ids $id --query Reservations[0].Instances[0].PublicDnsName)
	dnsName=${dnsName//\"}


	#try to transfer a file to the server
	if [[ $2 = "transferScript" ]];
	then
		while true; do
			scp -i $keyName.pem -o StrictHostKeyChecking=no $scriptName ec2-user@$dnsName:~
			#exitstatus 1 -> something went wrong -> keep trying
			if [ "$?" = "1" ];
			then
				echo "Filecopy failed! Trying again..."
				sleep 2
			else
				break
			fi
		done
		scp -i $keyName.pem -o StrictHostKeyChecking=no $keyName.pem ec2-user@$dnsName:~
	fi

	#try to transfer a file to the server
	if [[ $2 = "vm1tvm2" ]];
	then
		while true; do
			scp -i $keyName.pem -o StrictHostKeyChecking=no $scriptName ec2-user@$dnsName:~
			#exitstatus 1 -> something went wrong -> keep trying
			if [ "$?" = "1" ];
			then
				echo "Filecopy failed! Trying again..."
				sleep 2
			else
				break
			fi
		done
		rm VM1toVM2.txt > /dev/null 2>&1
		for (( i=1; i<6; i++ ))
		do
			startTime=$(date +%s%N)
			scp -i $keyName.pem -o StrictHostKeyChecking=no $i.dat ec2-user@$dnsName:~/fromVM
			endTime=$(date +%s%N)	
			totalTime=$(expr $endTime - $startTime)
			totalTime=$(expr $totalTime / 1000000)
			printf '%s\n' "$totalTime ms for file $i.data" >> VM1toVM2.txt
		done
		cat VM1toVM2.txt

	fi

	#the script to be executed with the ssh-req
	echo "Connecting via SSH..."
	#try connecting via ssh until success
	while true; do
		#connect via ssh with script-exec instruction once connected
		ssh -i "$keyName.pem" -o StrictHostKeyChecking=no ec2-user@$dnsName $1
		if [ "$?" = "255" ];
		then
			echo "Connection refused! Trying again..."
			sleep 2
		else
			break
		fi
	done
	if [[ $2 = "vm1tvm2" ]];
	then
		scp -i $keyName.pem -o StrictHostKeyChecking=no ec2-user@$dnsName:S3toVM2.txt ~;
		echo VM1toS3 > fin.txt;
		cat VM1toS3.txt >> fin.txt;
		echo VM1toVM2 >> fin.txt;
		cat VM1toVM2.txt >> fin.txt;
		echo S3toVM2 >> fin.txt;
		cat S3toVM2.txt >> fin.txt;
	fi
}

if [[ $1 = "simpleCreate" ]];
then
	startNewInstance "$script2" "vm1tvm2"
elif [[ $1 = "vm1ts3" ]];
then
	rm VM1toS3.txt > /dev/null 2>&1
	for (( i=1; i<6; i++ ))
	do
		startTime=$(date +%s%N)
		aws s3 cp $i.dat s3://$yourBucketName;
		endTime=$(date +%s%N)	
		totalTime=$(expr $endTime - $startTime)
		totalTime=$(expr $totalTime / 1000000)
		printf '%s\n' "$totalTime ms for file $i.data" >> VM1toS3.txt
	done
	cat VM1toS3.txt
elif [[ $1 = "s3tvm2" ]];
then
	rm S3toVM2.txt > /dev/null 2>&1
	for (( i=1; i<6; i++ ))
	do
		startTime=$(date +%s%N)
		aws s3 cp s3://$yourBucketName/$i.dat ~/fromBucket/$i.dat;
		endTime=$(date +%s%N)	
		totalTime=$(expr $endTime - $startTime)
		totalTime=$(expr $totalTime / 1000000)
		printf '%s\n' "$totalTime ms for file $i.data" >> S3toVM2.txt
	done
	cat S3toVM2.txt
else
	startNewInstance "$script1" "transferScript"
	scp -i $keyName.pem -o StrictHostKeyChecking=no ec2-user@$dnsName:fin.txt $(pwd);
fi








