#!/bin/bash
# Rultor release versioning script for Maven projects..
#
# It looks for the project’s version, which MUST respect the pattern 
# [0-9]*\.[0-9]*\.[0-9]*-SNAPSHOT and BE THE FIRST MATCH in pom.xml
#
# What it does: updates the pom.xml version of the project according to
# the variable ${tag} provided to rultor. Specifically, it increments the 
# 3rd digit and adds '-SNAPSHOT' to it.
#
# IMPORTANT:
#     the given tag has to contain 3 numbers separated by dots!
#     
#     e.g. tag = 1.0.1 or tag = 3.2.53 will result in new versions of 1.0.2-SNAPSHOT
#     or 3.2.54-SNAPSHOT
set -e
set -o pipefail

CURRENT_VERSION=$(grep -o '[0-9]*\.[0-9]*\.[0-9]*-SNAPSHOT' -m 1 pom.xml)

NUMBERS=($(echo $tag | grep -o -E '[0-9]+'))

echo "CURRENT VERSION IS"
echo $CURRENT_VERSION

NEXT_VERSION=${NUMBERS[0]}'.'${NUMBERS[1]}'.'$((${NUMBERS[2]}+1))'-SNAPSHOT'

echo "RELEASE VERSION IS"
echo $tag

echo "NEXT VERSION IS"
echo $NEXT_VERSION

whereis java
java --version
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

echo "************** JAVA HOME ***************"
echo $JAVA_HOME
whereis java
java --version
echo "************** END JAVA HOME ***********"

#Right after the project's <version> tag there has to be the comment <!--rrv-sed-flag--> which simplifies the sed regex bellow. 
#If the flag comment wouldn't be there, we'd have to write a more complicated regex to catch the artifactif from a row up.
#This is because only a regex for version tag would change all the matching version tags in the file.
sed -i "s/<version>${CURRENT_VERSION}<\/version><\!--rrv-sed-flag-->/<version>${tag}<\/version><\!--rrv-sed-flag-->/" pom.xml
mvn clean deploy -Prelease --settings /home/r/settings.xml
sed -i "s/<version>${tag}<\/version><\!--rrv-sed-flag-->/<version>${NEXT_VERSION}<\/version><\!--rrv-sed-flag-->/" pom.xml
sed -i "s/<version>.*<\/version>/<version>${tag}<\/version>/" README.md
sed -i "s/<a.*>fat<\/a>/<a href=\"https:\/\/oss\.sonatype\.org\/service\/local\/repositories\/releases\/content\/com\/amihaiemil\/web\/docker-java-api\/${tag}\/docker-java-api-${tag}-jar-with-dependencies\.jar\">fat<\/a>/" README.md

git commit -am "${NEXT_VERSION}"
git checkout master
git merge __rultor
git checkout __rultor

