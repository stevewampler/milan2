package com.sgw.milan2.markets

import com.sgw.milan2.Clock

import scala.collection.mutable

case class Market(name: String, clock: Clock) {
  private var _nextOrderId: Long = 0
  private val assetPairToBookMap: mutable.Map[AssetPair, Book] = mutable.Map()

  def getBookFor(assetPair: AssetPair): Book = synchronized {
    assetPairToBookMap.getOrElseUpdate(assetPair, Book(assetPair))
  }

  private def nextOrderId: Long = synchronized {
    val orderId = _nextOrderId
    _nextOrderId = _nextOrderId + 1
    orderId
  }

  def createMarketOrder(
    direction: Direction,
    quantity: Quantity,
    assetPair: AssetPair
  ): MarketOrder = synchronized {
    val order = MarketOrder(
      id = nextOrderId,
      market = this,
      placedAtTime = clock.now,
      direction = direction,
      quantity = quantity,
      assetPair = assetPair
    )

    val book = getBookFor(order.assetPair)

    book.fill(order)

    order
  }

  def createLimitOrder(
    direction: Direction,
    quantity: Quantity,
    assetPair: AssetPair,
    price: Price
  ): Order = {
    val order = LimitOrder(
      id = nextOrderId,
      market = this,
      placedAtTime = clock.now,
      direction = direction,
      quantity = quantity,
      price = price,
      assetPair = assetPair
    )

    val book = getBookFor(order.assetPair)

    book.fill(order)

    if (order.notFilled) {
      book.add(order)

      // TODO handle limit orders with a timeout
    }

    order
  }
}