### docker-java-api

[![Build Status](https://travis-ci.org/amihaiemil/docker-java-api.svg?branch=master)](https://travis-ci.org/amihaiemil/docker-java-api)
[![Coverage Status](https://coveralls.io/repos/github/amihaiemil/docker-java-api/badge.svg?branch=master)](https://coveralls.io/github/amihaiemil/docker-java-api?branch=master)

[![DevOps By Rultor.com](http://www.rultor.com/b/amihaiemil/docker-java-api)](http://www.rultor.com/p/amihaiemil/docker-java-api)
[![We recommend IntelliJ IDEA](http://amihaiemil.github.io/images/intellij-idea-recommend.svg)](https://www.jetbrains.com/idea/)

Lightweight, object oriented, Docker client for Java

A Java library for the Docker Engine API. It is the equivalent of the ``docker`` command-line client, for Java applications.

Unlike other docker clients for Java, this one aims to be as lightweight as possible, with as few transitive dependencies as possible and it should cause absolutely no runtime conflicts with other frameworks or platforms like Java EE. 

One other target is to be as intuitive as possbile, with the minimum usage of builder pattern and maximum usage of Interfaces and best OOP practices. 

### Contributing 

If you would like to contribute, just open an issue or a PR.

Make sure the maven build:

``$mvn clean install -Pcheckstyle``

passes before making a PR. [Checkstyle](http://checkstyle.sourceforge.net/) will make sure
you're following our code style and guidlines.