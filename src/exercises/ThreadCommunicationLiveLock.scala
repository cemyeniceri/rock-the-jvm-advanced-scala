package exercises

object ThreadCommunicationLiveLock extends App {

  case class Friend(name: String) {

    var side = "right"
    def switchSide(): Unit = {
      if(side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass....")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  new Thread(() => sam.pass(pierre)).start()   // sam's lock,      | then pierre's lock
  new Thread(() => pierre.pass(sam)).start()   // pierre's lock    | then sam's lock
}
