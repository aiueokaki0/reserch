#!/bin/sh

cd ./TeamFormation/RSLB2/boot/
while [[ 1 == 1 ]]; do
./start.sh  -b -v -c example -m kobe -s example &
sleep 60
killall java
./start.sh  -b -v -c example -m paris -s example-nopolice &
sleep 60
killall java
done
exit 0
