package cash.atto.work.processor.pubsub


import cash.atto.work.WorkGenerated
import cash.atto.work.WorkRequested
import cash.atto.work.adapter.WorkAdapter
import com.google.cloud.spring.pubsub.core.PubSubTemplate
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.function.Consumer


@Component
@Profile(value = ["pubsub", "test"])
class PubSubProcessor(
    private val workAdapter: WorkAdapter,
    private val properties: PubSubProperties,
    private val pubSubTemplate: PubSubTemplate,
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        val consumer = Consumer<ConvertedBasicAcknowledgeablePubsubMessage<WorkRequested>> {
            Thread.startVirtualThread {
                val workRequested = it.payload
                logger.info { "$workRequested" }
                try {
                    val work = workAdapter.request(workRequested.hash, workRequested.threshold)
                    val workGenerated = WorkGenerated(
                        callbackUrl = workRequested.callbackUrl,
                        hash = workRequested.hash,
                        threshold = workRequested.threshold,
                        work = work
                    )
                    logger.info { "$workGenerated" }
                    pubSubTemplate.publish(properties.workGeneratedTopic, workGenerated)
                    it.ack()
                } catch (e: Exception) {
                    logger.error(e) { "Callback failed for $workRequested" }
                }
            }
        }


        pubSubTemplate
            .subscribeAndConvert(properties.workRequestedSubscription!!, consumer, WorkRequested::class.java)
            .awaitRunning()
    }
}