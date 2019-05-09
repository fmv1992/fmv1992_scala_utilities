// Renamed package according to sections "Packages and Imports" & "Creating
// a package".
package fmv1992.fmv1992_scala_utilities.game_of_life

import org.scalatest.FunSuite

import fmv1992.fmv1992_scala_utilities.cli.GNUParser
import fmv1992.fmv1992_scala_utilities.cli.Argument

// Se matchers here:
// http://www.scalatest.org/user_guide/using_matchers#checkingObjectIdentity

object TestGameOfLifeHelper {

  val a = Alive(-1, -1)
  val d = Dead(-1, -1)

  val standardGOL = GameOfLife(1)

  val infiniteGameOfLife = Main.infiniteGameOfLife(1)

  def buildAliveNeighbours(i: Int): List[Cell] = {
    List.fill(i)(a) ++ List.fill(8 - i)(d)
  }

  def buildDeadNeighbours(i: Int): List[Cell] = {
    buildAliveNeighbours(8 - i)
  }

  val lAliveNeighbourCases = List.tabulate(9)(x ⇒ buildAliveNeighbours(x))
  val cCases = List(a, d)

  val tinyGameStr = """
  |oox
  |xxo
  |oxx""".stripMargin.trim
  val tinyGameNextManualStr = """
  |xox
  |oxx
  |xxx""".stripMargin.trim

  val tinyGame = GameOfLife(tinyGameStr)
  val tinyGameNextManual = GameOfLife(tinyGameNextManualStr)

  val stretchedGameStr = """
  |oxx
  |oox
  |xxo""".stripMargin.trim
  val stretchedGameNextManualStr = """
  |oox
  |oox
  |xox""".stripMargin.trim

  val stretchedGame = GameOfLife(stretchedGameStr)
  val stretchedGameNextManual = GameOfLife(stretchedGameNextManualStr)

}

class TestGameOfLife extends FunSuite {

  val h = TestGameOfLifeHelper

  test("Ensure constants across the same package version.") {
    assert(h.standardGOL.toString == "oxxxx\nxxooo\nxxoxo\noooox\nxoxxx")
  }

  test("The most basic tests.") {
    val aliveGame = GameOfLife("o")
    val deadGame = GameOfLife("x")
    assert(aliveGame.next === deadGame)
    assert(deadGame.next === deadGame)
  }

  test("Test all neighbours combinations.") {
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(0)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(1)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(2)) === h.a
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(3)) === h.a
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(4)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(5)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(6)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.a, h.lAliveNeighbourCases(7)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(0)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(1)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(2)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(3)) === h.a
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(4)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(5)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(6)) === h.d
    )
    assert(
      Cell.getNextStateFromNeighbours(h.d, h.lAliveNeighbourCases(7)) === h.d
    )
  }

  test("Test counting of cells.") {
    assert(
      List.tabulate(9)(identity) == h.lAliveNeighbourCases
        .map(Cell.countAliveCells)
    )
  }

  test("Manual case 01.") {
    assert(h.stretchedGame.next.toString === h.stretchedGameNextManualStr)
  }

  test("Manual case 02.") {
    assert(h.tinyGame.toString == h.tinyGameStr)
    assert(h.tinyGameNextManual.toString == h.tinyGameNextManualStr)

    val defectiveMutation = h.tinyGame.state(1)(0)
    val defectiveMutated =
      Cell.getNextState(defectiveMutation, h.tinyGame.state)
    assert(defectiveMutation === Dead(1, 0))
    assert(defectiveMutation.x === defectiveMutated.x)
    assert(defectiveMutation.y === defectiveMutated.y)

    assert(h.tinyGame.next.toString == h.tinyGameNextManual.toString)
  }

  test("Test an infinite stream of games.") {
    assert(h.infiniteGameOfLife.take(1)(0) === h.standardGOL)
    assert(h.infiniteGameOfLife.take(2).last === h.standardGOL.next)
    assert(h.infiniteGameOfLife.take(3).last === h.standardGOL.next.next)
  }

  test("Test if our initial game ever ends.") {
    assert(h.infiniteGameOfLife.take(1)(0) === h.standardGOL)
    assert(h.infiniteGameOfLife.take(2).last === h.standardGOL.next)
    assert(h.infiniteGameOfLife.take(3).last === h.standardGOL.next.next)
    assert(h.infiniteGameOfLife.exists(_.isOver))
  }

  // ???: Problem with reproduction of these tests. Approximately 1 in 20 enter
  // an endless loop.
  test("Test testableMain.") {

    // Test parsing.
    val parser = GNUParser(Main.CLIConfigPath)

    val finiteArguments = parser.parse(List("--ngames", "7"))

    // // Test API.
    Main.testableMain(finiteArguments)

    // // Assert that with no random seed two different games will be different.
    assert(
      Main.testableMain(finiteArguments) != Main.testableMain(finiteArguments)
    )

    // Assert that random seed determines games..
    def deterministicArguments(x: Int): Seq[Argument] = {
      parser.parse(s"--ngames ${x} --seed 987".split(" ").toList)
    }
    assert(Main.testableMain(deterministicArguments(0)) != Seq(""))
    assert(
      Main.testableMain(deterministicArguments(1)) == Main
        .testableMain(deterministicArguments(1))
    )
    assert(
      Main.testableMain(deterministicArguments(10)) == Main
        .testableMain(deterministicArguments(10))
    )

  }

}

class TestGameOfLifeOscillators extends FunSuite {

  test("Oscillator 01.") {

    val o01 = GameOfLife(GameOfLifeOscillators.oscillator01)
    val stream01 = Main.infiniteGameOfLife(o01)
    val stream02 = stream01.tail.tail
    stream01
      .zip(stream02)
      .take(100)
      .foreach(x ⇒ assert(x._1.toString == x._2.toString))

  }

}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
