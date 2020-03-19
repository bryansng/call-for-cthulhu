#!/bin/bash

# mvn package
# java -cp target/Generator-1.jar Generator-1

mvn package

# mvn exec:java
java -jar target/Generator.jar
