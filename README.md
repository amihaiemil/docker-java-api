### docker-java-api

[![Managed by Zerocrat](https://www.0crat.com/badge/G6LPQQV2P.svg)](https://www.0crat.com/p/G6LPQQV2P)

[![Build Status](https://travis-ci.org/amihaiemil/docker-java-api.svg?branch=master)](https://travis-ci.org/amihaiemil/docker-java-api)
[![Coverage Status](https://coveralls.io/repos/github/amihaiemil/docker-java-api/badge.svg?branch=master)](https://coveralls.io/github/amihaiemil/docker-java-api?branch=master)

[![Donate via Zerocracy](https://www.0crat.com/contrib-badge/G6LPQQV2P.svg)](https://www.0crat.com/contrib/G6LPQQV2P)
[![DevOps By Rultor.com](http://www.rultor.com/b/amihaiemil/docker-java-api)](http://www.rultor.com/p/amihaiemil/docker-java-api)
[![We recommend IntelliJ IDEA](http://amihaiemil.github.io/images/intellij-idea-recommend.svg)](https://www.jetbrains.com/idea/)

Lightweight, object-oriented, Docker client for Java

A Java library for the Docker Engine API. It is the equivalent of the ``docker`` command-line client, for Java applications.

Unlike other docker clients for Java, this one aims to be as lightweight as possible, with as few transitive dependencies as possible and it should cause absolutely no runtime conflicts with other frameworks or platforms like Java EE. 

Another target is that this library should be a true API, not an SDK. Read [this](http://www.amihaiemil.com/2018/03/10/java-api-for-docker.html) blog post and [the wiki](https://github.com/amihaiemil/docker-java-api/wiki) for more details.

### Maven dependency

The library comes as a maven dependency:

```
<dependency>
    <groupId>com.amihaiemil.web</groupId>
    <artifactId>docker-java-api</artifactId>
    <version>0.0.6</version>
</dependency>
```

**In order for it to work, you need to have an implementation of [JSON-P (JSR 374)](https://javaee.github.io/jsonp/index.html) in your classpath (it doesn't come transitively since most people are using Java EE APIs so, chances are it is already provided!).**

If you are not using Maven, you can also download the <a href="https://oss.sonatype.org/service/local/repositories/releases/content/com/amihaiemil/web/docker-java-api/0.0.6/docker-java-api-0.0.6-jar-with-dependencies.jar">fat</a> jar.

### Contributing 

If you would like to contribute, just open an issue or a PR.

Make sure the maven build:

``$mvn clean install -Pcheckstyle``

passes before making a PR. [Checkstyle](http://checkstyle.sourceforge.net/) will make sure
you're following our code style and guidlines.

### Running Integration Tests

In order to run the integration tests add the ``itcases`` profile to the maven command:

``$mvn clean install -Pcheckstyle -Pitcases``

Docker has to be intalled on the machine, with the default configuration, in order for the IT cases to work.
