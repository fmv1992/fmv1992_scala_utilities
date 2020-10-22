// Renamed package according to sections "Packages and Imports" & "Creating
// a package".
package fmv1992.fmv1992_scala_utilities.uniq

import org.scalatest.FunSuite

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

  val s3 = multiLineStringtoSeq(Stream.fill(10)("a").mkString("\n"))

}

// import org.scalatest.Matchers
//
// Se matchers here:
// http://www.scalatest.org/user_guide/using_matchers#checkingObjectIdentity
class TestUniq extends FunSuite {

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

    assert(Uniq.filterUnique(TestCases.s3).mkString("") == "a")

  }

  test("Test infinite iteration.") {

    // ???: Test that Stream is being used in a memory efficient way.
    // val megabytes500: Int = 500 * 1024 * 1024
    // assert(Uniq.filterUnique(
    // Stream.continually("e"))
    // .take(megabytes500).toList == List("e"))

  }

}
