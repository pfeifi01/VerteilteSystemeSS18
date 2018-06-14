#!/bin/bash
echo -n "Are you sure that you want to TERMINATE all instances (y/n)? "
read answer

if [ "$answer" != "${answer#[Yy]}" ] ;then
    while IFS='' read -r line || [[ -n "$line" ]]; do
    echo "Shutting down: $line"
	aws ec2 terminate-instances --instance-ids $line &
	foldername=$(echo -n $line | sha1sum)
	foldername=$(echo "$foldername" | tr -cd '[[:alnum:]]')
	rm -r $foldername
	printf '%s\n' $line >> ids/terminatedIds.txt
done < ids/ids.txt
rm ids/ids.txt
else
	echo aborted
fi
