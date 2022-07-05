import Endpoints.allEndPoints
import sttp.tapir.serverless.aws.sam.{AwsSamInterpreter, AwsSamOptions, CodeSource}

import java.io.PrintWriter

object LamdaSamTemplate extends App {

  val Array(runtime, zipLocation, handler, filename) = args

  val templateYaml: String = AwsSamInterpreter(
    AwsSamOptions(
      "",
      CodeSource(runtime, zipLocation, handler)
    )
  ).toSamTemplate(allEndPoints).toYaml

  new PrintWriter(filename) { write(templateYaml); close() }

}
