import Endpoints.{byeEndpoint, helloEndpoint}
import cats.implicits.catsSyntaxEitherId
import sttp.tapir.serverless.aws.lambda._
import sttp.tapir.serverless.aws.lambda.js._

import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.scalajs.js.annotation._

object LambdaMain {

  @nowarn
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val options: AwsServerOptions[Future] = AwsServerOptions(encodeResponseBody = false, Nil)

  val route: Route[Future] = AwsFutureServerInterpreter(options).toRoute(
    List(
      helloEndpoint.serverLogic { _ =>
        Future("hello".asRight[Unit])
      },
      byeEndpoint.serverLogic { _ =>
        Future("bye".asRight[Unit])
      }
    )
  )

  @JSExportTopLevel(name = "handler")
  def handler(event: AwsJsRequest, context: Any): scala.scalajs.js.Promise[AwsJsResponse] =
    AwsJsRouteHandler.futureHandler(event, route)
}
