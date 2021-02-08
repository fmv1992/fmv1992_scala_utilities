package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

class TestUtilities extends AnyFunSuite {

  test("Test `getContiguousElementsIndexes`.") {
    val s1 = Seq(0, 0, 0)
    val idx1 = Utilities.getContiguousElementsIndexes(s1)
    val reconstituted1 = idx1.flatMap(x => s1.slice(x._1, x._2))
    assert(reconstituted1 === s1)
    val s2 = Seq(0, 0, 0, true, true, false)
    val idx2 = Utilities.getContiguousElementsIndexes(s2)
    val reconstituted2 = idx2.flatMap(x => s2.slice(x._1, x._2))
    assert(reconstituted2 === s2)
    assert(Utilities.getContiguousElementsIndexes(Seq.empty) === Seq.empty)
  }

  test("Test scala version.") {
    val (major: Int, minor: Int, patch: Int) = Utilities.getScalaVersion
    assert(major >= 2)
  }

}
