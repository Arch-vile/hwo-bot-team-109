#!/bin/bash
JAVA=`which java| head -1`
$JAVA -cp bin:./lib/gson-2.2.2.jar fi.nakoradio.hwo.main.HWOBot $1 $2 $3 &
