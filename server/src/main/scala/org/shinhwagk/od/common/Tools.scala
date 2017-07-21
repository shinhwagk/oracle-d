package org.shinhwagk.od.common

object Tools {
  def readFile(path: String): Iterator[String] = {
    scala.io.Source.fromResource(path).getLines().map(_.trim)
  }

  def readSqlFile(path: String): String = {
    readFile(path).mkString(" ")
  }

  def readHtmlFile(path: String): String = {
    readFile(path).toString()
  }

}
