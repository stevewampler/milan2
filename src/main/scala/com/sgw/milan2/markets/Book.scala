package com.sgw.milan2.markets

import scala.collection.mutable

case class Book(assetPair: AssetPair) {
  private val bids: mutable.SortedSet[LimitOrder] = mutable.TreeSet[LimitOrder]()(BidOrderBookOrdering)
  private val asks: mutable.SortedSet[LimitOrder] = mutable.TreeSet[LimitOrder]()(AskOrderBookOrdering)

  def topBidOrder: Option[Order] = bids.synchronized { bids.headOption }
  def topAskOrder: Option[Order] = asks.synchronized { asks.headOption }

  def topBid: Option[(Quantity, Price)] = bids.synchronized { ??? }
  def topAsk: Option[(Quantity, Price)] = asks.synchronized { ??? }

  // this should only be called by a Market
  def add(order: LimitOrder): Book = {
    assert(order.isPending)
    assert(order.assetPair == assetPair)

    if (order.isBid) {
      bids.synchronized { bids.add(order) }
    } else {
      asks.synchronized { asks.add(order) }
    }

    this
  }

  private def bidPriceCompare(order: Order)(bid: Order): Boolean = bid.price.value >= order.price.value
  private def askPriceCompare(order: Order)(ask: Order): Boolean = ask.price.value <= order.price.value

  // this should only be called by a Market
  def fill(order: Order): Seq[Trade] = {
    val (side, priceCompare) = if (order.isBid) {
      (asks, askPriceCompare(order))
    } else {
      (bids, bidPriceCompare(order))
    }

    side.synchronized {
      side.foldLeft(List[Trade]()) { case (trades, bookOrder) =>
        if (priceCompare(bookOrder) && order.notFilled) {
          val fillQuantity = Quantity(order.quantity.value.min(bookOrder.quantity.value))
          val fillPrice = bookOrder.price

          if (fillQuantity.value > 0) {
            order.fill(fillQuantity, fillPrice)
            bookOrder.fill(fillQuantity, fillPrice)

            if (bookOrder.isFilled) {
              side.remove(bookOrder)
            }

            Trade(order, bookOrder, fillQuantity, fillPrice) :: trades
          } else {
            trades
          }
        } else {
          trades
        }
      }
    }
  }
}
