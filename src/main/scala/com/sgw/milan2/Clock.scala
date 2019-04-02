package com.sgw.milan2

trait Task {
  def run: Unit
}

trait Clock {
  def now: Long

  def schedule(task: Task, at: Long): Task
}
