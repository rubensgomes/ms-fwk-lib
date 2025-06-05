package com.rubensgomes.msframework

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/** Class created to drive SpringBooTest in this library. */
@SpringBootApplication class MsFrameworkApp

private val log: Logger = LoggerFactory.getLogger(MsFrameworkApp::class.java)

fun main(args: Array<String>) {
    log.trace("Starting test application")
    runApplication<MsFrameworkApp>(*args)
}
