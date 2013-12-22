#!/bin/sh
comment=$(git log -1)
version=$(mvn help:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')
cd databugger-core
dch --package databugger -D ldstack-nightly -v $version "$comment"
comment="$(echo "$comment"|tr -d '\n')"
sed -i "1s/^/ * $comment\n/" debian/CHANGES.txt
mvn clean install -DskipTests -Pdebpackage
