#!/bin/bash

find . -name "desktop.ini" -type f -delete		# remove all desktop.ini files.

# mvn package
# java -cp target/Generator-1.jar Generator-1

mvn clean
mvn package

# mvn exec:java
java -jar target/Generator.jar
