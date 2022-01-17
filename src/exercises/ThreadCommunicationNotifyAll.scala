package exercises

object ThreadCommunicationNotifyAll extends App {

  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread - $i] waiting...")
        bell.wait()
        println(s"[thread - $i] hooray!!!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock'n roll!")
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()
  }

  testNotifyAll()
}
