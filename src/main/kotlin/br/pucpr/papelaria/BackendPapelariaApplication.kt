package br.pucpr.papelaria

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendPapelariaApplication

fun main(args: Array<String>) {
	runApplication<BackendPapelariaApplication>(*args)
}
