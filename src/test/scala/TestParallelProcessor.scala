package fi.hsci

import org.junit.Assert._
import org.junit.Test

import scala.util.{Failure, Success}

class TestParallelProcessor {

  @Test
  def testSuccess: Unit = {
    val t = new ParallelProcessor
    t.feedAndProcessFedTasksInParallel(() => {
      for (i <- 1 to t.queueCapacity*2*t.numWorkers)
        t.addTask("test "+i,() => { })
    }) match {
      case Success(_) =>
      case Failure(e) => fail("Failed tasks: "+e.getMessage)
    }
  }

  def testTasksDoNotBlockEachOther: Unit = {
    val t = new ParallelProcessor
    t.feedAndProcessFedTasksInParallel(() => {
      for (i <- 1 to 100) {
        t.addTask("test "+i,() => { print(".") })
        t.addTask("test "+i,() => { print(".") })
        t.addTask("test "+i,() => { print(".") })
        t.addTask("test "+i,() => { print(".") })
        t.addTask("test "+i,() => { print(".") })
        t.addTask("test "+i,() => {
          Thread.sleep(40)
          print("X")
        })
        t.addTask("test "+i,() => {
          Thread.sleep(40)
          print("X")
        })
      }
    }) match {
      case Success(_) =>
      case Failure(e) => fail("Failed tasks: "+e.getMessage)
    }
  }

  @Test
  def testTaskFailure: Unit = {
    val t = new ParallelProcessor
    t.feedAndProcessFedTasksInParallel(() => {
      for (i <- 1 to t.queueCapacity * 2 * t.numWorkers)
        t.addTask("test " + i, () => {})
      t.addTask("failure", () => {
        throw new Exception("Failure!")
      })
      for (i <- 1 to t.queueCapacity * 2 * t.numWorkers)
        t.addTask("test " + i, () => {})
    }) match {
      case Success(_) => fail("Succeeded when was meant to fail")
      case Failure(_) =>
    }
  }

  @Test
  def testFeederFailure: Unit = {
    val t = new ParallelProcessor
    t.feedAndProcessFedTasksInParallel(() => {
      for (i <- 1 to t.queueCapacity*2*t.numWorkers)
        t.addTask("test "+i,() => { })
      throw new Exception("Failure!")
    }) match {
      case Success(_) => fail("Succeeded when was meant to fail")
      case Failure(e) =>
    }
  }

  @Test
  def testMultiFailures: Unit = {
    val t = new ParallelProcessor
    t.feedAndProcessFedTasksInParallel(() => {
      for (i <- 1 to t.queueCapacity*2*t.numWorkers)
        t.addTask("test "+i,() => { })
      t.addTask("failure", () => {
        throw new Exception("Failure!")
      })
      t.addTask("failure", () => {
        throw new Exception("Failure!")
      })
      for (i <- 1 to t.queueCapacity * 2 * t.numWorkers)
        t.addTask("test " + i, () => {})
      throw new Exception("Failure!")
    }) match {
      case Success(_) => fail("Succeeded when was meant to fail")
      case Failure(e) =>
    }
  }

}
