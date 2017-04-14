package com.pepeground.core.support

import scalikejdbc._

object PostgreSQLSyntaxSupport {

  implicit class RichInsertSQLBuilder(val self: InsertSQLBuilder) extends AnyVal {
    def onConflictUpdate(constraint: String)(columnsAndValues: (SQLSyntax, Any)*): InsertSQLBuilder = {
      val cvs = columnsAndValues map {
        case (c, v) => sqls"$c = $v"
      }
      self.append(sqls"ON CONFLICT ON CONSTRAINT ${SQLSyntax.createUnsafely(constraint)} DO UPDATE SET ${sqls.csv(cvs: _*)}")
    }

    def onConflictDoNothing(): InsertSQLBuilder = {
      self.append(sqls"ON CONFLICT DO NOTHING")
    }
  }

  implicit class RichSQLSyntax(val self: sqls.type) extends AnyVal {
    def values(column: SQLSyntax): SQLSyntax = sqls"values($column)"
  }

}