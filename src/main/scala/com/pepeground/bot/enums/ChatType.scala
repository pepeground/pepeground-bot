package com.pepeground.bot.enums

object ChatType {
  def apply(v: Int): String = {
    v match {
      case 0 => "chat"
      case 1 => "faction"
      case 2 => "supergroup"
      case 3 => "channel"
      case _ => "chat"
    }
  }

  def apply(v: String): Int = {
    v match {
      case "chat" => 0
      case "faction" => 1
      case "supergroup" => 2
      case "channel" => 3
      case _ => 0
    }
  }
}