package com.mcurse.domain

import cats.Monad

object DomainLogic {

  def helloLogic[F[_]: Monad]: Unit => F[Either[Unit, String]] = _ => Monad[F].pure(Right("hello"))
  def byeLogic[F[_]: Monad]: Unit => F[Either[Unit, String]]   = _ => Monad[F].pure(Right("bye"))

}
