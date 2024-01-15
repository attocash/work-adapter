package cash.atto.work

import cash.atto.commons.serialiazers.json.AttoJson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@Configuration
class ApplicationConfiguration {

    @Bean
    fun kotlinSerializationJsonHttpMessageConverter(): HttpMessageConverter<*> {
        return KotlinSerializationJsonHttpMessageConverter(AttoJson)
    }

}