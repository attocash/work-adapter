package cash.atto.work.adapter

import cash.atto.commons.AttoHash
import cash.atto.commons.AttoWork
import cash.atto.commons.fromHexToByteArray
import io.swagger.v3.oas.annotations.Operation
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/works")
class WorkController(private val adapter: WorkAdapter) {
    @PostMapping("{hash}")
    @Operation(description = "Process request for work generation")
    suspend fun generate(@PathVariable hash: String, @RequestBody request: WorkRequest): WorkResponse {
        return WorkResponse(adapter.request(AttoHash(hash.fromHexToByteArray()), request.threshold))
    }
}

@Serializable
data class WorkRequest(val threshold: ULong)

@Serializable
data class WorkResponse(@Contextual val work: AttoWork)