#!/usr/bin/env bash

for i in `ls endpoints` ; do ./automaticTests.sh endpoints/$i ; done
