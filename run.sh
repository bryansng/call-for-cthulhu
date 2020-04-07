#!/bin/bash

find . -name "desktop.ini" -type f -delete		# remove all desktop.ini files. (Google Backup and Sync files)

mvn clean
mvn package

java -jar target/CallForCthulu.jar
