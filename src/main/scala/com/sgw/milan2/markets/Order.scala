package com.sgw.milan2.markets

import scala.collection.mutable
import scala.util.Try

sealed trait OrderStatus
object Pending extends OrderStatus
object PartiallyFilled extends OrderStatus
object Filled extends OrderStatus
object Canceling extends OrderStatus
object Canceled extends OrderStatus

case class Quantity(value: Double) extends AnyVal {
  def +(quantity: Quantity) = Quantity(value + quantity.value)
  def -(quantity: Quantity) = Quantity(value - quantity.value)
}

case class Price(value: Double) extends AnyVal

object Price {
  val MaxPrice = Price(Double.MaxValue)
  val MinPrice = Price(0.0)
}

case class Fill(quantity: Quantity, atPrice: Price)

sealed trait Direction
object Buy extends Direction
object Sell extends Direction

trait Order {
  val id: Long
  val placedAtTime: Long
  val direction: Direction
  val quantity: Quantity
  val market: Market
  val assetPair: AssetPair
  val fills = mutable.ArrayBuffer[Fill]()

  def isBid: Boolean = direction == Buy
  def isAsk: Boolean = direction == Sell

  def isPending: Boolean = filledQuantity.value == 0
  def isPartiallyFilled: Boolean = !isPending && !isFilled
  def isFilled: Boolean = filledQuantity.value == quantity.value
  def notFilled: Boolean = !isFilled

  def price: Price

  def filledQuantity: Quantity = Quantity(fills.map(_.quantity.value).sum)
  def remainingQuantity: Quantity = Quantity(quantity.value - fills.map(_.quantity.value).sum)

  def fill(quantity: Quantity, price: Price): Order = fill(Fill(quantity, price))

  def fill(fill: Fill): Order = synchronized {
    fills += fill
    this
  }

  def cancel: Try[Order] = ???
}

case class MarketOrder(
  id: Long,
  market: Market,
  placedAtTime: Long,
  direction: Direction,
  quantity: Quantity,
  assetPair: AssetPair
) extends Order {

  val price: Price = if (isBid) {
    Price.MaxPrice
  } else {
    Price.MinPrice
  }
}

case class LimitOrder(
  id: Long,
  market: Market,
  placedAtTime: Long,
  direction: Direction,
  quantity: Quantity,
  price: Price,
  assetPair: AssetPair
) extends Order
