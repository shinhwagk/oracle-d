package test

import java.io.File

import scala.io.Source

object TestOpenFile extends App {

  val file = new File(".")
  println(file.getAbsolutePath)
}
