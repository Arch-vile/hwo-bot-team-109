#!/bin/bash
JAVAC=`which javac| head -1`
$JAVAC -target 1.6 -source 1.6 -d bin -sourcepath ./src/ -cp ./lib/gson-2.2.2.jar:./lib/slf4j-api-1.7.0.jar:./lib/jbox2d-library-2.1.2.2.jar:./lib/jbox2d-testbed-2.1.2.2.jar:./lib/jbox2d-serialization-1.0.0.jar:./lib/log4j-1.2.17.jar src/fi/nakoradio/hwo/main/HWOBot.java 
