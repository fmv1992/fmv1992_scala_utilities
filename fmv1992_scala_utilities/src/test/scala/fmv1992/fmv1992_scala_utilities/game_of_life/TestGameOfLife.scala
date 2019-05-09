// Renamed package according to sections "Packages and Imports" & "Creating
// a package".
package fmv1992.fmv1992_scala_utilities.game_of_life

import org.scalatest.FunSuite

import fmv1992.fmv1992_scala_utilities.cli.GNUParser
import fmv1992.fmv1992_scala_utilities.cli.Argument

// Se matchers here:
// http://www.scalatest.org/user_guide/using_matchers#checkingObjectIdentity

trait TestGameOfLifeHelper {

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

  // Test parsing.
  val parser = GNUParser(Main.CLIConfigPath)

  val finiteArguments = parser.parse(List("--n-games", "7"))

}

class TestGameOfLife extends FunSuite with TestGameOfLifeHelper {

  test("Ensure constants across the same package version.") {
    assert(standardGOL.toString == "oxxxx\nxxooo\nxxoxo\noooox\nxoxxx")
  }

  test("The most basic tests.") {
    val aliveGame = GameOfLife("o")
    val deadGame = GameOfLife("x")
    assert(aliveGame.next === deadGame)
    assert(deadGame.next === deadGame)
  }

  test("Test all neighbours combinations.") {
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(0)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(1)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(2)) === a
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(3)) === a
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(4)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(5)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(6)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(a, lAliveNeighbourCases(7)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(0)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(1)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(2)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(3)) === a
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(4)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(5)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(6)) === d
    )
    assert(
      Cell.getNextStateFromNeighbours(d, lAliveNeighbourCases(7)) === d
    )
  }

  test("Test counting of cells.") {
    assert(
      List.tabulate(9)(identity) == lAliveNeighbourCases
        .map(Cell.countAliveCells)
    )
  }

  test("Manual case 01.") {
    assert(stretchedGame.next.toString === stretchedGameNextManualStr)
  }

  test("Manual case 02.") {
    assert(tinyGame.toString == tinyGameStr)
    assert(tinyGameNextManual.toString == tinyGameNextManualStr)

    val defectiveMutation = tinyGame.state(1)(0)
    val defectiveMutated =
      Cell.getNextState(defectiveMutation, tinyGame.state)
    assert(defectiveMutation === Dead(1, 0))
    assert(defectiveMutation.x === defectiveMutated.x)
    assert(defectiveMutation.y === defectiveMutated.y)

    assert(tinyGame.next.toString == tinyGameNextManual.toString)
  }

  test("Test an infinite stream of games.") {
    assert(infiniteGameOfLife.take(1)(0) === standardGOL)
    assert(infiniteGameOfLife.take(2).last === standardGOL.next)
    assert(infiniteGameOfLife.take(3).last === standardGOL.next.next)
  }

  test("Test if our initial game ever ends.") {
    assert(infiniteGameOfLife.take(1)(0) === standardGOL)
    assert(infiniteGameOfLife.take(2).last === standardGOL.next)
    assert(infiniteGameOfLife.take(3).last === standardGOL.next.next)
    assert(infiniteGameOfLife.exists(_.isOver))
  }

  // ???: Problem with reproduction of these tests. Approximately 1 in 20 enter
  // an endless loop.
  test("Test testableMain.") {

    // // Test API.
    Main.testableMain(finiteArguments)

    // // Assert that with no random seed two different games will be different.
    assert(
      Main.testableMain(finiteArguments) != Main.testableMain(finiteArguments)
    )

    // Assert that random seed determines games..
    def deterministicArguments(x: Int): Seq[Argument] = {
      parser.parse(s"--n-games ${x} --seed 987".split(" ").toList)
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

class TestGameOfLifeOscillators extends FunSuite  with TestGameOfLifeHelper {

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

class TestGameOfLifePoolOfCases extends FunSuite  with TestGameOfLifeHelper {

 val defectiveSeed01 = 1785599012
 val defectiveSeed02 = -709633859
 val defectiveSeed03 = -92084096

test("Test that seeding these crahses current GOL.") {

    val arg1: Seq[Argument] = {
      parser.parse(s"--make-games --n-games 10 --seed 1785599012".split(" ").toList)
    }
    println(Main.testableMain(arg1))
}

}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
