package cash.atto.work.adapter

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


data class WorkServerRequest(val action: String = "work_generate", val hash: String, val threshold: String)
data class WorkServerResponse(val work: String)

@Service
class WorkAdapter(private val workAdapterProperties: WorkAdapterProperties, private val restTemplate: RestTemplate) {
    fun request(hash: String, threshold: ULong): String {
        val request = WorkServerRequest(hash = hash, threshold = threshold.toString())
        return restTemplate.postForObject(workAdapterProperties.url, request, WorkServerResponse::class.java)!!.work
    }

}