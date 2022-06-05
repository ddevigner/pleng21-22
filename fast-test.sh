#!/bin/bash

java -jar adac_4.jar ../testbench/compilado/${1}.adac
../testbench/ensamblador ../testbench/compilado/$1
../testbench/maquinap ../testbench/compilado/$1