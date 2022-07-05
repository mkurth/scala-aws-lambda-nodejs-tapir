import de.perseus.aws.lamda.events.{ApiGatewayEvent, ApiGatewayResponse, Context}
import io.circe.parser.*
import io.circe.generic.auto._
import io.circe.syntax._

import scalajs.js
import js.annotation.JSExportTopLevel
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

object LambdaMain {

  def main(event: ApiGatewayEvent): ApiGatewayResponse = {
    ApiGatewayResponse(
      body = "hello " + event.pathParameters,
      headers = Map("Content-Type" -> "text/plain"))
  }

  @JSExportTopLevel(name = "handler")
  val handler: js.Function2[String, Context, String] = { (rawEvent: String, _: Context) =>
    decode[ApiGatewayEvent](rawEvent) match
      case Left(value) => ApiGatewayResponse(statusCode = 400, body = value.toString).asJson.toString
      case Right(event) => main(event).asJson.toString
  }
}
