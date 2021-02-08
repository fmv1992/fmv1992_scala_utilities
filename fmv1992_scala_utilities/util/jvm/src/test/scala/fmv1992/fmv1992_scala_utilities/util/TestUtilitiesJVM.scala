package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

class TestUtilitiesJVM extends AnyFunSuite {

  test("Test `isScalaNative`.") {
    // "Scala Native"
    assert(Utilities.isScalaNative === false)
  }

  test("Test `isScalaJVM`.") {
    // "OpenJDK 64-Bit Server VM"
    assert(Utilities.isScalaJVM === true)
  }

}
