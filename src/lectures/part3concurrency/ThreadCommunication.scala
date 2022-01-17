package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /*
     the producer-consumer problem

     producer -> [ ? ] -> problem
  */
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(n: Int): Unit = value = n

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] waiting")
      while (container.isEmpty) {
        println("[consumer] actively waiting")
      }
      println(s"[consumer] I had the value ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] computing")
      Thread.sleep(100)
      val value = 42
      println("[producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // SYNCHRONIZED ADVANCED
  /*
    val someObject = "hello"
    someObject.synchronized {   // lock the object's monitor
      // code                   // any other thread trying to run this will block
    }                           // release the lock
  */

  // Only AnyRefs can have synchronized blocks
  /*
    - make no assumptions about who gets the lock first
    - keep locking to a minimum
    - maintain thread safety at ALL times in parallel applications
    -
  */

  /*
    - wait and notify
  */
  /* thread - 1
    val someObject = "hello"
    someObject.synchronized {   // lock the object's monitor
      // ... code part 1
      someObject.wait()         // release the lock and ...wait
      // ... code part 2        // when allowed the proceed,lock the monitor again and continue
    }                           // release the lock
  */

  /* thread - 2
    someObject.synchronized {   // lock the object's monitor
      // ... code part 1
      someObject.notify()       // signal ONE sleeping thread they may continue (!!! Which thread ?? You don't know!!!!) -> you can use notifyAll() to notify all threads --
      // ... code part 2        // when allowed the proceed,lock the monitor again and continue
    }                           // but only after I'm done and unlock the monitor
  */

  def smartProdCons(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] waiting")
      container.synchronized {
        container.wait()
      }
      println(s"[consumer] I had the value ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] Hard at work, boring")
      Thread.sleep(100)
      val value = 42
      container.synchronized {
        println("[producer] I have produced, after long work, the value " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  /*
    producer -> [ ? ? ? ? ] -> consumer
  */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          // hey producer, there's empty space, are you lazy!!!!!
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println("[producer] producing...")
          buffer.enqueue(i)

          // hey consumer, new food for you!!
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(250))
      }
    })

    consumer.start()
    producer.start()
  }

  println("-----------naiveProdCons-----------")
  naiveProdCons()
  Thread.sleep(1000)
  println("-----------smartProdCons-----------")
  smartProdCons()
  Thread.sleep(1000)
  println("-----------prodConsLargeBuffer-----------")
  prodConsLargeBuffer()
}
