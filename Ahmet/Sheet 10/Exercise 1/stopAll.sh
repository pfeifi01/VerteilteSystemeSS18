#!/bin/bash
while IFS='' read -r line || [[ -n "$line" ]]; do
    echo "Shutting down: $line"
	aws ec2 stop-instances --instance-ids $line &
done < ids/ids.txt