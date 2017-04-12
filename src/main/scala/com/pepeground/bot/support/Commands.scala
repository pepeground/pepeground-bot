package com.pepeground.bot.support

trait ResponseCommand
class Reply(val str: Option[String]) extends ResponseCommand
class Answer(val str: Option[String]) extends ResponseCommand
class Silence extends ResponseCommand