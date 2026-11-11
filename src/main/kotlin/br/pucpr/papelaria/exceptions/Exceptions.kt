package br.pucpr.papelaria.exceptions

// 404
open class NaoEncontradoException(msg: String) : RuntimeException(msg)
class ProdutoNaoEncontradoException(id: Long) : NaoEncontradoException("Produto $id nao encontrado")

// 400
open class RequisicaoInvalidaException(msg: String) : RuntimeException(msg)
class PedidoInvalidoException(msg: String) : RequisicaoInvalidaException(msg)

// 409: a operacao conflita com o estado atual (registro em uso, duplicado...)
class ConflitoException(msg: String) : RuntimeException(msg)
