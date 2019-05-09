// Renamed package according to sections "Packages and Imports" & "Creating
// a package".

// IMPORTANT: see "projectnote01".
package fmv1992.fmv1992_scala_utilities.util

object Utilities {

  def getContiguousElementsIndexes[A](l: List[A]): List[(Int, Int)] = {
    def go(
        gl: List[A],
        curIndex: Int,
        indexCounter: Int,
        curEl: A,
        acc: List[(Int, Int)]
    ): List[(Int, Int)] = {
      val nextIndexCounter = indexCounter + 1
      gl match {
        case h :: t ⇒ {
          if (curEl == h) {
            go(t, curIndex, nextIndexCounter, curEl, acc)
          } else {
            go(
              t,
              nextIndexCounter,
              nextIndexCounter,
              h,
              (curIndex, nextIndexCounter) +: acc
            )
          }
        }
        case Nil ⇒ (curIndex, nextIndexCounter) +: acc
      }
    }
    go(l.tail, 0, 0, l.head, Nil).reverse
  }

}

//  Run this in vim:
//
// vim source: 1,$-10s/=>/⇒/ge
//
// vim: set filetype=scala fileformat=unix foldmarker={,} nowrap tabstop=2 softtabstop=2 spell:
