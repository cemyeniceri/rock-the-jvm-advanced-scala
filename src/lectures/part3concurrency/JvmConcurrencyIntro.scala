package lectures.part3concurrency

import java.util.concurrent.Executors

object JvmConcurrencyIntro extends App {

  // JVM Threads

  private val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel!!!")
  }

  val aThread = new Thread(runnable)

  aThread.start() // gives the signal to the JVM to start a JVM thread

  // create a JVM thread => OS thread
  runnable.run() // doesn't do anything in parallel, It just run it in same thread.
  aThread.join() // blocks until aThread finishes running

  val aThreadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  val aThreadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("GoodBye")))
  aThreadHello.start()
  aThreadGoodBye.start()

  // but don't forget that creating and killing a thread is really expensive!!!
  // solution is reuse them with executors!!
  val pool = Executors.newFixedThreadPool(10)

  pool.execute(() => {
    Thread.sleep(2000)
    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(2000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 second")
  })

  pool.shutdown() // don't accept anymore execute!!!
  // pool.execute(() => println("Shouldn't appear")) // throws an exception in the calling (main) thread

  // pool.shutdownNow() // shutdown pool immediately even there are some thread is waiting to be executed

  println(pool.isShutdown)
}