package cash.atto.work.adapter

import cash.atto.commons.AttoHash
import cash.atto.commons.AttoWork
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Serializable
data class WorkServerRequest(
    val action: String = "work_generate",
    @Contextual val hash: AttoHash,
    val threshold: String
)

@Serializable
data class WorkServerResponse(@Contextual val work: AttoWork)

@Service
class WorkAdapter(private val workAdapterProperties: WorkAdapterProperties, private val restTemplate: RestTemplate) {
    fun request(hash: AttoHash, threshold: ULong): AttoWork {
        val request = WorkServerRequest(hash = hash, threshold = threshold.toString())
        return restTemplate.postForObject(workAdapterProperties.url, request, WorkServerResponse::class.java)!!.work
    }

}