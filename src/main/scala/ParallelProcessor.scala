package fi.hsci

import com.typesafe.scalalogging.LazyLogging

import java.io.{File, PrintWriter, StringWriter}
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}


class ParallelProcessor extends LazyLogging {

  val numWorkers = sys.runtime.availableProcessors
  val availableMemory = Runtime.getRuntime.maxMemory - (Runtime.getRuntime.totalMemory - Runtime.getRuntime.freeMemory)
  val queueCapacity = 1024
  private val fjp = new ForkJoinPool(numWorkers, (pool: ForkJoinPool) => {
    val worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool)
    worker.setName("indexing-worker-" + worker.getPoolIndex)
    worker
  }, null, true)
  private val indexingTaskExecutionContext = ExecutionContext.fromExecutorService(fjp)

  /** helper function to get a recursive stream of files for a directory */
  def getFileTree(f: File): LazyList[File] =
    f #:: (if (f.isDirectory) f.listFiles().sorted.to(LazyList).flatMap(getFileTree)
    else LazyList.empty)

  def getFileTreeSize(path: String): Long = getFileTree(new File(path)).foldLeft(0L)((s,f) => s+f.length)

  def getStackTraceAsString(t: Throwable) = {
    val sw = new StringWriter
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  def createHashDirectories(dest: String): Unit = {
    for (
      i <- 0 to 9;
      j <- 0 to 9) new File(dest+"/"+i+"/"+j).mkdirs()
  }

  private var tasks = 0
  private val completedTasks = new AtomicInteger()

  private val all = Promise[Unit]()
  private val failures = new ArrayBuffer[Throwable]

  def addTask(id: String, taskFunction: () => Unit): Unit = {
    tasks += 1
    while (fjp.getQueuedSubmissionCount>queueCapacity) Thread.sleep(500)
    Future {
      try {
        taskFunction()
      } catch {
        case cause: Exception =>
          logger.error("An error has occurred processing source "+id, cause)
          val e = new Exception("An error has occurred processing source "+id, cause)
          all.tryFailure(e)
          failures synchronized { failures += e }
          throw e
      } finally {
        completedTasks.incrementAndGet()
      }
    }(indexingTaskExecutionContext)
  }

  var startTime = -1L

  def durationToString(milliseconds: Long): String = {
    val str = new StringBuilder()
    var seconds = milliseconds / 1000
    if (seconds >= 3600*24) {
      str.append(seconds/(3600*24))
      str.append("d")
      seconds = seconds % (3600*24)
    }
    if (seconds >= 3600) {
      str.append(seconds/3600)
      str.append("h")
      seconds = seconds % 3600
    }
    if (seconds >= 60) {
      str.append(seconds/60)
      str.append("m")
      seconds = seconds % 60
    }
    str.append(seconds)
    str.append("s")
    str.toString()
  }

  def feedAndProcessFedTasksInParallel(taskFeeder: () => Unit): Try[Unit] = {
    implicit val iec = ExecutionContext.Implicits.global
    val fed = Promise[Unit]()
    startTime = System.currentTimeMillis()
    val sf = Future {
      taskFeeder()
      logger.info(f"Feeding tasks ended successfully at ${durationToString(System.currentTimeMillis()-startTime)}%s, producing a total of $tasks%,d tasks.")
      if (!fed.isCompleted) fed.trySuccess(())
    }
    sf.onComplete {
      case Failure(t) =>
        logger.error("Feeding tasks ended in an error:" + t.getMessage,t)
        failures synchronized { failures += t }
        fed.tryFailure(t)
        all.tryFailure(t)
      case Success(_) =>
    }
    Await.ready(fed.future, Duration.Inf)
    while (completedTasks.get()<tasks) Thread.sleep(1000)
    all.trySuccess(())
    Await.ready(all.future,Duration.Inf)
    all.future.value.get match {
      case Success(_) => logger.info(f"Successfully processed all ${completedTasks.get()}%,d tasks in ${durationToString(System.currentTimeMillis()-startTime)}%s.")
      case Failure(_) =>
        logger.error(f"Processing ${completedTasks.get()}%,d tasks resulted in ${failures.size} errors." )
        for (failure <- failures) logger.error("Error:",failure)
    }
    indexingTaskExecutionContext.shutdown()
    all.future.value.get
  }

  def runSequenceInOtherThread(tasks: (() => Unit)*): Future[Unit] = Future {
    for (task <- tasks) task()
  }(ExecutionContext.Implicits.global)

  def waitForTasks(tasks: Future[Unit]*) {
    for (task <- tasks) Try(Await.result(task, Duration.Inf)).toEither.left.foreach(logger.error("Task encountered exception",_))
  }


}