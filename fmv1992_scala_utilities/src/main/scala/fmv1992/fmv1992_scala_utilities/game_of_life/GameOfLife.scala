package fmv1992.fmv1992_scala_utilities.game_of_life

import fmv1992.fmv1992_scala_utilities.cli.Argument
import fmv1992.fmv1992_scala_utilities.cli.CLIConfigTestableMain

import fmv1992.fmv1992_scala_utilities.util.Reader

/** General concept of game. */
trait Game {

  val isOver: Boolean

  val representation: String

}

/** Conway's game of life.
  *
  * @see [[https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life]]
  *
  * Rules:
  *
  * 1. Any live cell with fewer than two live neighbours dies, as if by
  * underpopulation.
  * 2. Any live cell with two or three live neighbours lives on to the next
  * generation.
  * 3. Any live cell with more than three live neighbours dies, as if by
  * overpopulation.
  * 4. Any dead cell with exactly three live neighbours becomes a live cell, as
  * if by reproduction.
  */
case class GameOfLife(state: Seq[Seq[Cell]]) extends Game {

  lazy val isOver: Boolean = {

    state.flatten.forall(
      c =>
        c match {
          case _: Dead  => true
          case _: Alive => false
        }
    )
  }

  lazy val representation: String = {
    val res = state.map(_.mkString).mkString("\n")
    res
  }

  override def toString: String = this.representation

  /** Get next state for this game of life. */
  def next: GameOfLife = {
    GameOfLife(this.state.map(l => l.map(c => Cell.getNextState(c, state))))
  }

}

/** Companion object for Game of Life. */
object GameOfLife {

  // ???: Tests must be "resistant" to these (parametrize!).
  val xdim = 5
  val ydim = 5

  private def getCells(r: scala.util.Random): Seq[Seq[Cell]] = {
    val listOfCells = Seq.tabulate(xdim)(
      x =>
        Seq.tabulate(ydim)(y => {
          val c: Cell = if (r.nextBoolean()) Alive(x, y) else Dead(x, y)
          c
        })
    )
    listOfCells.toSeq
  }

  def apply(seed: Int): GameOfLife = {
    val rState = new scala.util.Random(seed)
    val cells = getCells(rState)
    GameOfLife(cells)
  }

  def apply(repr: String): GameOfLife = {

    val splitted = repr.split('\n')
    val xdim = splitted(0).length - 1
    val ydim = splitted.length - 1

    val index: IndexedSeq[Tuple2[Int, Int]] =
      (0 to xdim).map(x => (0 to ydim).map(y => (x, y))).flatten

    val constructed = index.map(i => {
      val cAsString = splitted(i._1)(i._2).toChar
      cAsString match {
        case 'x' => Dead(i._1, i._2)
        case 'o' => Alive(i._1, i._2)
        case _   => throw new Exception()
      }
    })

    val constructedAsCells = constructed.grouped(xdim + 1).toSeq

    GameOfLife(constructedAsCells)

  }

}

/** Cell representation for game of life. */
trait Cell {

  val x: Int

  val y: Int

  val representation: Char

}

/** Companion object for Cell. */
object Cell {

  def countAliveCells(l: Seq[Cell]): Int = {

    val aliveCount: Int = l.count(c => {
      c match {
        case _: Alive => true
        case _: Dead  => false
        case _        => throw new Exception()
      }
    })

    aliveCount

  }

  def getNeighbours(cell: Cell, grid: Seq[Seq[Cell]]): Seq[Cell] = {

    val xlim = grid(0).length

    val ylim = grid.length

    def mapOutOfBoundsToDeadCells(p: Tuple2[Int, Int]): Cell = {
      val (x, y) = p
      if (x < 0 || y < 0 || y >= ylim || x >= xlim) {
        Dead(x, y)
      } else {
        grid(x)(y)
      }
    }

    lazy val xindexes = ((cell.x - 1) to (cell.x + 1)).toSeq
    lazy val yindexes = ((cell.y - 1) to (cell.y + 1)).toSeq
    lazy val cp = yindexes.flatMap(y => xindexes.map(x => (x, y)))
    // NOTE: Defective product here caused big bug...
    lazy val cpNotSelf =
      cp.filter(_ != (cell.x, cell.y)).map(x => mapOutOfBoundsToDeadCells(x))

    require(cpNotSelf.length == 8)
    cpNotSelf
  }

  def getNextStateFromNeighbours(cell: Cell, nb: Seq[Cell]): Cell = {

    val aliveNB: Int = countAliveCells(nb)

    def getNextGenStateForAlive(): Cell = {
      // If cell cell is alive.
      if (aliveNB < 2) {
        Dead(cell.x, cell.y)
      } else if (aliveNB == 2 || aliveNB == 3) {
        cell
      } else {
        Dead(cell.x, cell.y)
      }
    }

    def getNextGenStateForDead(): Cell = {
      if (aliveNB == 3) {
        Alive(cell.x, cell.y)
      } else cell
    }

    val res: Cell = cell match {
      case _: Alive => getNextGenStateForAlive
      case _: Dead  => getNextGenStateForDead
      case _        => throw new Exception()
    }

    require(res.x == cell.x)
    require(res.y == cell.y)

    res

  }

  def getNextState(cell: Cell, grid: Seq[Seq[Cell]]): Cell = {

    val nb = getNeighbours(cell, grid)

    getNextStateFromNeighbours(cell, nb)

  }

}

/** Alive cell. */
case class Alive(x: Int, y: Int, representation: Char = 'o') extends Cell {
  override def toString(): String = representation.toString
}

/** Dead cell. */
case class Dead(x: Int, y: Int, representation: Char = 'x') extends Cell {
  override def toString(): String = representation.toString
}

/** Main program. */
object Main extends CLIConfigTestableMain {

  val version = Reader.readLines("./src/main/resources/version").mkString

  val programName = "GameOfLife"

  val CLIConfigPath = "./src/main/resources/game_of_life_cli_config.conf"

  // ???: Uniformize the juggling of Int → Random and vice versa in this
  // package.
  def getInfiniteStreamOfGames(i: Int): Stream[String] = {

    def origStream: Stream[GameOfLife] = infiniteGameOfLife(i)
    // ???: Print at least one dead game.
    def truncEndingStream: Stream[GameOfLife] = origStream.takeWhile(!_.isOver)

    // ???: Games take O(n) memory.
    type SGOL = Set[GameOfLife]
    lazy val previousGames: Stream[SGOL] = {
      (Set.empty: Set[GameOfLife]) #::
        previousGames
          .zip(truncEndingStream)
          .map({ case (a: SGOL, b: GameOfLife) => a + b })
    }
    def truncNonRepeatingStream: Stream[GameOfLife] =
      previousGames
        .zip(truncEndingStream)
        .takeWhile({ case (a: SGOL, b: GameOfLife) => !a.contains(b) })
        .map(_._2)

    // Lazily append streams.
    val r = new scala.util.Random(i)
    truncNonRepeatingStream.map(_.toString) #::: getInfiniteStreamOfGames(
      r.nextInt
    )

  }

  def findCycles(): Seq[String] = {
    ???
  }

  /** Testable interface for main program. */
  def testableMain(args: Seq[Argument]): Seq[String] = {

    val (seedString, o1) = splitArgumentFromOthers(args, "seed")
    val (findCycle, o2) = splitArgumentFromOthers(o1, "find-cyclic")
    val (makeGames, o3) = splitArgumentFromOthers(o2, "make-games")
    val (nGamesString, o4) = splitArgumentFromOthers(o3, "n-games")

    val nGames: Int = nGamesString(0).value(0).toInt

    // Fix seed.
    val seedInt: Int =
      scala.util
        .Try(seedString(0).value(0).toInt)
        .getOrElse(System.nanoTime.toInt)

    // action.

    val res: Seq[String] = args.foldLeft(Seq.empty: Seq[String])((l, a) => {
      if (a.longName == "find-cyclic") {
        findCycles()
      } else if (a.longName == "make-games") {
        getInfiniteStreamOfGames(seedInt).take(nGames)
      } else {
        l
      }
    })

    res

  }

  def infiniteGameOfLife(seed: Int = 0): Stream[GameOfLife] = {
    val first = GameOfLife(seed)
    infiniteGameOfLife(first)
  }

  def infiniteGameOfLife(game: GameOfLife): Stream[GameOfLife] = {
    def s1: Stream[GameOfLife] = game #:: s1.map(_.next)
    s1
  }

}
