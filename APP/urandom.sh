#!/bin/bash

for i in {1..1000}
do
cat /dev/urandom | nc localhost 1234
done
