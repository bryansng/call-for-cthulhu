#!/bin/bash

find . -name "desktop.ini" -type f -delete		# remove all desktop.ini files. (Google Backup and Sync files)

if [ "$1" == "dev" ]; then
	mvn clean javafx:run
else
	mvn clean
	# mvn package -Dmaven.test.skip=true
	mvn package

	java -jar target/CallForCthulu.jar
	# mvn exec:java
fi
