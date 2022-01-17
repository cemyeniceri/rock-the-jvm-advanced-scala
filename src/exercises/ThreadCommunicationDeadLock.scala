package exercises

object ThreadCommunicationDeadLock extends App {

  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen!")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other!")
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  new Thread(() => sam.bow(pierre)).start()   // sam's lock,      | then pierre's lock
  new Thread(() => pierre.bow(sam)).start()   // pierre's lock    | then sam's lock
}
