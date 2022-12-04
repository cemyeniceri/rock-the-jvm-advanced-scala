package lectures.part4implicits

object TypeClasses extends App {

  trait HtmlWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HtmlWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  User("Cem", 32, "cem.yeniceri@unicraft.io").toHtml

  /* This is a bad design! */

  /**
   * 1- This is only valid for the types WE write (you can't extend java.util.date, etc..)
   * 2- One implementation out of quite a number
   * */

  /* option 2 - pattern matching */
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, g, a) => ???
      //case java.util.Date => ???
      case _ => ???
    }
  }

  /* Still not the best design */

  /**
   * 1- lost the type safety
   * 2- need to modify the code evert time
   * 3- still ONE implementation for each class type!!
   * */


  /* BETTER DESIGN */
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  private val cem: User = User("Cem", 32, "cem.yeniceri@unicraft.io")
  UserSerializer.serialize(cem)

  // 1- We can define serializers for other types

  import java.util.Date

  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2- We can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}/> </div>"
  }

  // part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(5)(IntSerializer)) // same
  println(HTMLSerializer.serialize(5)) // same because it knows that IntSerializer is implicit companion object
  println(HTMLSerializer.serialize(cem))

  // provides accessing to the entire type class interface
  println(HTMLSerializer[User].serialize(cem))

  // part 3
  implicit class HtmlEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(cem.toHTML) // println(new HtmlEnrichment[User](john).toHtml(UserSerializer))
  // COOL !!

  /**
   * - Extend to new types
   * - choose implementation
   * - super expressive!
   * */

  println(2.toHTML)
  println(cem.toHTML(PartialUserSerializer))

  /**
   * - type class itself --- HTMLSerializer[T] {...}
   * - type class instances (some of which are implicit) --- UserSerializer, IntSerializer
   * - conversion with implicit classes --- HTMLEnrichment
   * */

  // context bounds
  def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

  def htmlSugar[T: HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

  //implicitly
  case class Permissions(mask: String)

  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of code
  val standardPerms = implicitly[Permissions]


}
