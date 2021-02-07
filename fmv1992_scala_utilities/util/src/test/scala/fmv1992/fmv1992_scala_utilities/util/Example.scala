package fmv1992.fmv1992_scala_utilities.util

import java.io.File

import fmv1992.scala_cli_parser.StandardParser

object Example {

  val reader01Path: String = "./util/src/test/resources/test_reader_01.txt"
  val reader01File: File = new File(reader01Path)
  require(reader01File.exists, reader01File.getCanonicalPath)

}
