package fmv1992.fmv1992_scala_utilities.util

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

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

  test("Test `findAll*`.") {
    val pwd = sys.env.get("PWD").getOrElse(throw new Exception())
    val allNodes =
      Utilities.findAllNodes(pwd)

    val knownDir = Paths.get(pwd, "project")
    val knownFiles =
      allNodes.filter(_.endsWith(this.getClass.getSimpleName + ".scala"))
    assert(knownFiles.length == 1)
    val knownFile = knownFiles(0)

    // Assert that it contains a known file, a known directory and not the
    // calling argument.
    assert(allNodes.contains(knownFile))
    assert(allNodes.contains(knownDir))
    assert(!allNodes.contains(pwd))

    // Assert that this is lazily evaluated.
  }

}
