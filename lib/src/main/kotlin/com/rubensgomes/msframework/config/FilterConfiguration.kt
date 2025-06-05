package com.rubensgomes.msframework.config

import com.rubensgomes.msframework.web.filter.MDCClearFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

/**
 * Registers various servlet filters to be used in the Web Server Filter Pipeline.
 *
 * @author Rubens Gomes
 */
@Configuration
open class FilterConfiguration {
    @Bean
    open fun mdcClearFilter(): FilterRegistrationBean<MDCClearFilter> {
        val bean = FilterRegistrationBean<MDCClearFilter>()
        bean.filter = MDCClearFilter()
        bean.order = Ordered.HIGHEST_PRECEDENCE
        bean.isEnabled = true
        bean.addUrlPatterns("/*")
        bean.setName(MDCClearFilter::class.java.name)
        return bean
    }
}
