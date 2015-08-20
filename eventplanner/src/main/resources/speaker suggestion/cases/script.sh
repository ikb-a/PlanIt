#!/bin/bash

#./script [from] [to]

for i in `seq $1 $2`; do
	mkdir $i
	cp 1/event.json $i
	cp 1/low.json $i
	cp 1/medium.json $i
	cp 1/high.json $i
done
