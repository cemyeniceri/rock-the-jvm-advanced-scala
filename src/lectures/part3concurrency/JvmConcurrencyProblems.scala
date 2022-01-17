package lectures.part3concurrency

object JvmConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0 // mutable evil!!!

    val aThread1 = new Thread(() => {
      x = 1
    })
    val aThread2 = new Thread(() => {
      x = 2
    })
    aThread1.start()
    aThread2.start()
    println(x) // race condition occurs
  }

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    Thread.sleep(5)

    /*
      involves 3 steps
        - read old value
        - compute result
        - write new value
    */
    bankAccount.amount -= price
  }

  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.synchronized { // does not allow multiple threads to run the critical section AT THE SAME TIME!!
      Thread.sleep(5)
      bankAccount.amount -= price // critical section
    }
  }

  /*
    Example race condition:
    thread1 (shoes)
      - reads amount 50000
      - compute result 50000 - 3000 = 47000
    thread2 (iphone)
      - reads amount 50000
      - compute result 50000 - 4000 = 46000
    thread1 (shoes)
      - write amount 47000
    thread2 (iphone)
      - write amount 46000
  */
  def demoBankingProblem(): Unit = {
    (1 to 20).foreach { _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buy(account, "shoes", 3000))
      val thread2 = new Thread(() => buy(account, "iPhone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000) println(s"AHA! I've just broken the bank : ${account.amount}")
    }
  }

  def demoBankingProblem2(): Unit = {
    (1 to 20).foreach { _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buySafe(account, "shoes", 3000))
      val thread2 = new Thread(() => buySafe(account, "iPhone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000) println(s"AHA! I've just broken the bank : ${account.amount}")
    }
  }

  /**
    Exercises
    1- create "inception threads"
      thread-1
        thread-2
          thread-3
            .....
      each thread prints "hello from thread $i"
      Print all messages IN REVERSE ORDER

    2- What's the max/min value of x ?
    3- "sleep fallacy": what's the value of message ?

  * */

  // 1 - inception threads
  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < maxThreads) {
        val childThread = inceptionThreads(maxThreads, i + 1)
        childThread.start()
        childThread.join()
      }
      println(s"hello from thread $i")
    })
  }

  // 2 - max/min
  // max value can be 100 - each thread increases x by 1
  // min value = 1 each thread update same x(0) as 1 at the same time
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
  }


  // 3 - "sleep fallacy"
  /*
    almost always, message = "Scala is awesome!"
    Obnoxious(terrible) situation (possible):

    main thread:
      message = "Scala sucks"
      awesomeThread.start()
      sleep(1001) - yields execution

    awesome thread:
      sleep(1000) - yields execution
    OS gives the CPU to some important thread, let's say it takes > 2s
    OS gives the CPU back to the main thread

    main thread:
      println(message) // "Scala sucks"
    awesome thread:
      message = "Scala is awesome" // but too late :)


    // solution: join the worker thread
  */

  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is awesome!"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1001)
    //awesomeThread.join()
    println(message)
  }

  def main(args: Array[String]): Unit = {
    runInParallel()
    demoBankingProblem()
    println("_______SAFE-BUY__________")
    demoBankingProblem2()

    println("_______EXAMPLES__________")
    inceptionThreads(50).start()
    demoSleepFallacy()
  }
}
