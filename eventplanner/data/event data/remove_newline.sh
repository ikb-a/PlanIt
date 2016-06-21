#!/bin/bash
#Supply a file and all of the newline characters will be replaced with space characters.
sed -i ':a;N;$!ba;s/\n/ /g' $1
