# NodeJS AWS lambda using Scala and Tapir 

## How to use

```
sbt universal:packageBin;createSamTemplate
sam deploy # or sam local start-api
```

## What it does

* Reads API definition from [Endpoints.scala](endpoints/src/main/scala/Endpoints.scala) which are written using tapir
* Translates the whole Scala project to a node application
* Creates a template.yml for AWS sam based on the code

| Project           | Description                                                                |
|-------------------|----------------------------------------------------------------------------|
| endpoints         | contains all API definitions independent of implementation                 |
| nodeJsServer      | combines endpoints and the actual implementation to serve                  |
| createSamTemplate | helper project which creates the sam deployment description `template.yml` |
| domain            | somewhat framework agnostic implementation of the business logic           |

## Why just scala 2.13 and not scala3?

Because [tapir-aws-lambda](https://mvnrepository.com/artifact/com.softwaremill.sttp.tapir/tapir-aws-lambda) isn't released yet for scala 3 on scalajs