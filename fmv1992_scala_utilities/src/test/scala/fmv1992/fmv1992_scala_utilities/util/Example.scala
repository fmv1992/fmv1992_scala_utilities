package fmv1992.fmv1992_scala_utilities.util

import java.io.File

import fmv1992.scala_cli_parser.StandardParser

object Example {

  // Provide examples for Reader. --- {

  val reader01Path: String = "./src/test/resources/test_reader_01.txt"
  val reader01File: File = new File(reader01Path)
  require(reader01File.exists, reader01File.getCanonicalPath)

  // --- }

  // Provide examples for CLIParser. --- {

  val cli01Path: String = "./src/test/resources/test_cli_example_01.txt"
  val cli01File: File = new File(cli01Path)
  require(cli01File.exists, cli01File.getCanonicalPath)
  val cli01Parser: StandardParser = StandardParser(cli01File)

  // --- }

  // Provide examples for GNUParser. --- {

  val cli02Path: String = "./src/test/resources/test_cli_example_02_gnu.txt"
  val cli02File: File = new File(cli02Path)
  require(cli02File.exists, cli02File.getCanonicalPath)

  // Invalid files.
  val cli03Path: String =
    "./src/test/resources/test_cli_example_03_no_help_gnu.txt"
  val cli03File: File = new File(cli03Path)
  require(cli03File.exists, cli03File.getCanonicalPath)

  val cli04Path: String =
    "./src/test/resources/test_cli_example_04_no_version_gnu.txt"
  val cli04File: File = new File(cli04Path)
  require(cli04File.exists, cli04File.getCanonicalPath)

  val cli06Path: String =
    "./src/test/resources/test_cli_example_06_default_argument.txt"
  val cli06File: File = new File(cli06Path)
  require(cli06File.exists, cli06File.getCanonicalPath)

  // --- }

  // Provide examples for TestableMain. --- {

  val cli05Path: String = "./src/test/resources/test_cli_example_05_sum.txt"
  val cli05File: File = new File(cli05Path)
  require(cli05File.exists, cli05File.getCanonicalPath)
  val cli05Parser: StandardParser = StandardParser(cli05File)

  // --- }

}
