package fmv1992.fmv1992_scala_utilities.util

import org.scalatest.funsuite.AnyFunSuite

class TestUtilitiesNative extends AnyFunSuite {

  test("Test `isScalaNative`.") {
    // "Scala Native"
    assert(Utilities.isScalaNative === true)
  }

  test("Test `isScalaJVM`.") {
    // "OpenJDK 64-Bit Server VM"
    assert(Utilities.isScalaJVM === false)
  }

}
