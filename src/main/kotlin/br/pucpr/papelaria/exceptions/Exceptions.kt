package br.pucpr.papelaria.exceptions

open class NaoEncontradoException(msg: String) : RuntimeException(msg)
class ProdutoNaoEncontradoException(id: Long) : NaoEncontradoException("Produto $id nao encontrado")
class PedidoInvalidoException(msg: String) : RuntimeException(msg)
