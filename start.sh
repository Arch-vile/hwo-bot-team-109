#!/bin/bash
JAVA=`which java| head -1`
$JAVA -cp target:./lib/gson-2.2.2.jar fi.nakoradio.hwo.main.HWOBot &