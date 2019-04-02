package com.sgw.milan2.markets

object AskOrderBookOrdering extends Ordering[LimitOrder] {
  def compare(o1: LimitOrder, o2: LimitOrder): Int = {
    val p1 = o1.price.value
    val p2 = o2.price.value

    if (p1 == p2) {
      val time1 = o1.placedAtTime
      val time2 = o2.placedAtTime

      if (time1 == time2) {
        0
      } else if (time1 < time2) {
        1
      } else {
        -1
      }
    } else if (p1 > p2) {
      -1
    } else {
      1
    }
  }
}
