package com.sgw.milan2

trait Event {
  def run: Unit
}

object EventQueue {

  def enqueue(event: Event): Event = ???

  private val thread = new Thread(runnable).start()
}
