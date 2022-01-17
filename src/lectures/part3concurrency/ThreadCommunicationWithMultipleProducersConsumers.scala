package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunicationWithMultipleProducersConsumers extends App {

  /*
    producer1 -> [ ? ? ? ? ] -> consumer1
    producer2 -> ...^     ^.... consumer2
  */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      /*
        producer produces value, two Cons are waiting
        notifies ONE consumer, notifies on buffer
      */
      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[consumer - $id] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer - $id] consumed " + x)

          // buffer.notify()           // this can notify either a producer or another consumer, that's why we changed  "if (buffer.isEmpty)" with "while (buffer.isEmpty)"....
          buffer.notifyAll() // this will wake up all
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer - $id] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[producer - $id] producing...")
          buffer.enqueue(i)

          // notifies everybody to wake up!!
          buffer.notifyAll()
          i += 1
        }
        Thread.sleep(random.nextInt(250))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    (1 to nConsumers).foreach(new Consumer(_, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  multiProdCons(3, 3)
}
