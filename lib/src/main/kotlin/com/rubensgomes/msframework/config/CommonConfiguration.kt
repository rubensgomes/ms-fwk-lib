package com.rubensgomes.msframework.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * A [@Configuration] class to be a place holder for common configurations to be appliced to all
 * applications.
 *
 * @author Rubens Gomes
 */
@Configuration
@EnableConfigurationProperties(*[WebProperties::class])
class CommonConfiguration
