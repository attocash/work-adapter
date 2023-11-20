package cash.atto.work.adapter

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/works")
class WorkController(private val adapter: WorkAdapter) {
    @PostMapping("{hash}")
    @Operation(description = "Process request for work generation")
    suspend fun generate(@PathVariable hash: String, @RequestBody request: WorkRequest): WorkResponse {
        return WorkResponse(adapter.request(hash, request.threshold))
    }
}

data class WorkRequest(val threshold: ULong)
data class WorkResponse(val work: String)