#!/usr/bin/env bash
cp core/src/test/resources/application.conf.example core/src/test/resources/application.conf
psql -c 'create database pepeground_test;' -U postgres
sbt core/test