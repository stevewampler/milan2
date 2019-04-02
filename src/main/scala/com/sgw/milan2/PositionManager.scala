package com.sgw.milan2

import com.sgw.milan2.markets.{Asset, Quantity}

import scala.collection.mutable

case class Position(asset: Asset) {
  private var quantity: Quantity = Quantity(0)

  def fill(addition: Quantity): Position = synchronized {
    quantity = Quantity(quantity.value + addition.value)

    this
  }
}

case class PositionManager() {
  private val assetToPositionMap: mutable.Map[Asset, Position] = mutable.Map()

  def getPositionFor(asset: Asset): Position = synchronized {
    assetToPositionMap.getOrElseUpdate(asset, Position(asset))
  }
}
