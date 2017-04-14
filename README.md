# Pepeground bot [![Build Status](https://travis-ci.org/pepeground/pepeground-bot.svg?branch=master)](https://travis-ci.org/pepeground/pepeground-bot)

Scala implementation of [shizoid](https://github.com/top4ek/shizoid)

## Requirements

* JDK 1.8
* PostgreSQL 9.2 +
* Redis Server 3.3.0 +

## Configuration

```
cp src/main/resources/application.conf.example /path/to/your/application.conf
vim /path/to/your/application.conf
```

## Running

```sh
sbt assembly
java -jar -Dconfig.file=/path/to/your/application.conf target/scala-2.12/bot-assembly-0.1.jar
```
