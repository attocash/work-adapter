package cash.atto.work.adapter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "atto.adapter")
class WorkAdapterProperties {
    var url: String = "http://localhost:8000"
    var connectTimeout: Long = 60_000
    var readTimeout: Long = 60_000
}