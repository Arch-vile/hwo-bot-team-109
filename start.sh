#!/bin/bash
JAVA=`which java| head -1`
$JAVA -cp bin:./lib/gson-2.2.2.jar:./lib/slf4j-api-1.7.0.jar:./lib/jbox2d-library-2.1.2.2.jar:./lib/jbox2d-testbed-2.1.2.2.jar:./lib/jbox2d-serialization-1.0.0.jar fi.nakoradio.hwo.main.HWOBot $1 $2 $3 &


