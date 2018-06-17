## Execution
To run the scripts run the following commands on your local machine. This will upload the scripts to the respective Virtual Machines.

    $ ./createVM1.sh 
    $ ./createVM2.sh 

Now at the VM1 run the following commands to execute the scripts:

    $ ./createFiles.sh 
    $ ./copyFromVM1ToS3.sh 

Then at the VM2 run the following command to execute the script:

    $ ./copyFromS3ToVM2.sh 

##Issues
> I had an issue starting an instance of *t2.micro*, but when the instance is already running I can connect to it.

> I couldn't provide an execution screenshot, because suddenly I got an error (*AWS was not able to validate the provided access credentials*) concerning my access credentials, which I didn't change in the meantime and I couldn't resolve this problem...