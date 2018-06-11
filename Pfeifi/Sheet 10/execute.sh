# Author: Martin Pfeifhofer © SS 2018
# start a timer, start t2 instance, install updates, upload file, compile and run file
# t2 setup parameters
imageid="ami-37501207"
instance_type="t2.micro"
key_name="distributedSystems2018PfeifhoferM"
security_group="launch-wizard-1"
wait_seconds="5"
port="22"
key_location="distributedSystems2018PfeifhoferM.pem"
user="user1"
region="us-west-2"
awsAccessKeyID="AKIAIGB5UF2YG6DIYASQ"
awsSecretAccessKey="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"
script="sudo yum update -y; sudo yum install gcc-c++ -y; g++ hello.cpp -o hello; ./hello"
# configure user
aws configure set AWS_ACCESS_KEY_ID $awsAccessKeyID 
aws configure set AWS_SECRET_ACCESS_KEY $awsSecretAccessKey
aws configure set default.region $region
# start timer
START_TIME=$SECONDS
# start t2 instance
echo "Starting instance..."
aws ec2 run-instances --image-id $imageid --count 1 --instance-type $instance_type --key-name $key_name --security-groups $security_group > /dev/null 2>&1
# wait for a public ip
while true; do
	ip=$(aws ec2 describe-instances --region $region | grep PublicIpAddress | grep -E -o "([0-9]{1,3}[\.]){3}[0-9]{1,3}")
	if [ ! -z "$ip" ]; then
		break
	else
		echo "Not found yet. Waiting for $wait_seconds more seconds."
		sleep $wait_seconds
	fi
done
# upload script
echo "Uploading Script..."
scp -i $key_name.pem -o StrictHostKeyChecking=no hello.cpp ec2-user@$ip
echo "Found IP $ip - Starting tunnel on port $port."
echo "Now download build essentials, compile script and run it"
sudo ssh -o StrictHostKeyChecking=no -i $key_location ec2-user@$ip; sudo yum update -y; sudo yum install gcc-c++ -y; sudo g++ hello.cpp -o hello; ./hello
# stop timer and print
ELAPSED_TIME=$(($SECONDS - $START_TIME))
echo "ELAPSED TIME: $(($ELAPSED_TIME/60)) min $(($ELAPSED_TIME%60)) sec"