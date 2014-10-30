#!/bin/sh
comment=$(git log -1)
version=$(mvn help:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')
cd rdfunit-validate
dch -v $version "$comment"
comment="$(echo "$comment"|tr -d '\n')"
sed -i "1s/^/ * $comment\n/" debian/CHANGES.txt
mvn clean install -DskipTests -Pdebpackage -am
