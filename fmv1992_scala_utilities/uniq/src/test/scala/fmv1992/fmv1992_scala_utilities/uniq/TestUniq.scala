// Renamed package according to sections "Packages and Imports" & "Creating
// a package".
package fmv1992.fmv1992_scala_utilities.uniq

// import fmv1992.fmv1992_scala_utilities.util.Example

import fmv1992.scala_cli_parser.GNUParser

import org.scalatest.funsuite.AnyFunSuite

object TestCases {

  def multiLineStringtoSeq: String => Seq[String] = {
    _.stripMargin.trim.split("\n").toSeq
  }

  val s1 = multiLineStringtoSeq("""a
    |b
    |c
    |a
    |a
    |b
    |d""")

  val s2 = multiLineStringtoSeq(s"""xyz\t
    |xyz
    |xyz
    |9999
    |&
    |9999
    |###
    |lastline""")

  val s3 = multiLineStringtoSeq(s"""a
    |
    |x
    |
    |
    |a""")

  val s4 = multiLineStringtoSeq(Stream.fill(10)("a").mkString("\n"))

}

// import org.scalatest.Matchers
//
// Se matchers here:
// http://www.scalatest.org/user_guide/using_matchers#checkingObjectIdentity
class TestUniq extends AnyFunSuite {

  test("Simplemost test.") {
    assert(
      Uniq.filterUnique(TestCases.s1.toSeq).mkString("")
        == "abcd"
    )

    lazy val copyOfs2 = Seq("xyz\t", "xyz", "9999", "&", "###", "lastline")
    assert(
      Uniq.filterUnique(TestCases.s2).mkString("")
        == copyOfs2.mkString("")
    )
    Uniq
      .filterUnique(TestCases.s2)
      .zip(copyOfs2)
      .foreach((x: Tuple2[String, String]) => assert(x._1 == x._2))

    assert(Uniq.filterUnique(TestCases.s4).mkString("") == "a")

  }

  test("--skip-empty-lines parameter") {
    assert(
      Uniq.filterUnique(TestCases.s3, skipEmptyLines = true)
        == Stream("a", "", "x", "", "")
    )

  }

  test("Test infinite iteration.") {

    // ???: Test that LazyList is being used in a memory efficient way.
    // val megabytes500: Int = 500 * 1024 * 1024
    // assert(Uniq.filterUnique(
    // LazyList.continually("e"))
    // .take(megabytes500).toList == List("e"))

  }

  test("Test main function.") {

    // ???: Also defined here:
    // `fmv1992_scala_utilities:092c0bc:fmv1992_scala_utilities/util/src/test/scala/fmv1992/fmv1992_scala_utilities/util/Example.scala:1`.
    val uniq01Path: String = "./util/src/test/resources/test_uniq_01.txt"

    // // ???: 🐛: This is a hack.
    // assert(
    //   Uniq.testableMain(
    //     GNUParser(Uniq.CLIConfigContents).parse(List("--input", uniq01Path))
    //   ) == "abce".toList.map(_.toString)
    // )
  }

}
