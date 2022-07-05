import sttp.tapir._

import scala.annotation.nowarn

@nowarn
object Endpoints {

  val byeEndpoint: Endpoint[Unit, Unit, Unit, String, Any] = endpoint.get
    .in("api" / "bye")
    .out(stringBody)

  val helloEndpoint: Endpoint[Unit, Unit, Unit, String, Any] = endpoint.get
    .in("hello")
    .out(stringBody)

  val allEndPoints: List[AnyEndpoint] = List(byeEndpoint, helloEndpoint)

}
