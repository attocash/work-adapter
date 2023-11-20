package cash.atto.work.adapter

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class WorkAdapterConfiguration {
    @Bean
    fun restTemplate(
        workAdapterProperties: WorkAdapterProperties,
        builder: RestTemplateBuilder
    ): RestTemplate {
        return builder
            .setConnectTimeout(Duration.ofMillis(workAdapterProperties.connectTimeout))
            .setReadTimeout(Duration.ofMillis(workAdapterProperties.readTimeout))
            .build()
    }

}