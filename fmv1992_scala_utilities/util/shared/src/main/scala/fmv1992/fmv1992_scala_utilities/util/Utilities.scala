// Renamed package according to sections "Packages and Imports" & "Creating
// a package".

// IMPORTANT: see "projectnote01".
package fmv1992.fmv1992_scala_utilities.util

object Utilities {

  def getContiguousElementsIndexes[A](l: Seq[A]): Seq[(Int, Int)] = {
    @scala.annotation.tailrec
    def go(
        gl: Seq[A],
        curIndex: Int,
        indexCounter: Int,
        curEl: A,
        acc: Seq[(Int, Int)],
    ): Seq[(Int, Int)] = {
      val nextIndexCounter = indexCounter + 1
      gl match {
        case h :: t => {
          if (curEl == h) {
            go(t, curIndex, nextIndexCounter, curEl, acc)
          } else {
            go(
              t,
              nextIndexCounter,
              nextIndexCounter,
              h,
              (curIndex, nextIndexCounter) +: acc,
            )
          }
        }
        case Nil => (curIndex, nextIndexCounter) +: acc
      }
    }
    if (l.isEmpty) Seq.empty else go(l.tail, 0, 0, l.head, Nil).reverse
  }

  def isScalaNative: Boolean = {
    scala.util.Properties.javaVmName.toLowerCase
      .filter(_.isLetter)
      .contains("scalanative")
  }

  def isScalaJVM: Boolean = {
    scala.util.Properties.javaVmName.toLowerCase
      .filter(_.isLetter)
      .contains("vm")
  }

  def getScalaVersion: (Int, Int, Int) = {
    val vs =
      scala.util.Properties.versionString.filter(x => x.isDigit || (x == '.'))
    val vint: Array[Int] = vs.split('.').map(x => x.toInt)
    vint match {
      case Array(a, b, c) => (a, b, c)
      case _              => throw new Exception(vint.toString)
    }
  }

}
