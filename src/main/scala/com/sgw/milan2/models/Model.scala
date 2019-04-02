package com.sgw.milan2.models

import com.sgw.milan2.markets.MarketManager
import com.sgw.milan2.{Clock, PositionManager, RiskManager, Task}

trait Model {
  val name: String
  val clock: Clock
  val riskManager: RiskManager
  val positionManager: PositionManager
  val marketManager: MarketManager
}

case class TestModel(
  name: String,
  clock: Clock,
  positionManager: PositionManager,
  marketManager: MarketManager
) {
  clock.schedule(
    new Task {
      def run {
        start
      }
    },
    at = 0
  )

  def start: Unit = {

  }
}
