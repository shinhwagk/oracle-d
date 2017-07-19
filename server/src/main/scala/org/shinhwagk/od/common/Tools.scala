package org.shinhwagk.od.common

import scala.io.BufferedSource

object Tools {
  def readFile(path: String): BufferedSource = {
    scala.io.Source.fromFile(path)
  }

  def readSqlFile(path: String): String = {
    readFile(path).mkString(" ")
  }

  def readHtmlFile(path: String): String = {
    readFile(path).toString()
  }

}
