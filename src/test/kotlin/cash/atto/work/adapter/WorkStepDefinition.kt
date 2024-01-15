package cash.atto.work.adapter

import cash.atto.commons.AttoNetwork
import cash.atto.commons.AttoOpenBlock
import cash.atto.commons.AttoWork
import cash.atto.work.*
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.google.cloud.spring.pubsub.core.PubSubTemplate
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue


class WorkStepDefinition(
    private val pubSubTemplate: PubSubTemplate,
) {

    @When("work is requested")
    fun request() {
        val shortKey = PropertyHolder.getActiveKey(AttoOpenBlock::class.java)!!
        val block = PropertyHolder.get(AttoOpenBlock::class.java, shortKey)
        val work = AttoWork.work(AttoNetwork.LOCAL, block.timestamp, block.hash)
        stubFor(
            post(urlEqualTo("/")).willReturn(
                aResponse().withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{ \"work\": \"$work\"}")
            )
        )

        val workRequested = WorkRequested(
            "http://mock-url:7777",
            block.hash,
            AttoWork.threshold(AttoNetwork.LOCAL, block.timestamp),
        )
        pubSubTemplate.publish(CucumberConfiguration.workRequestedTopic, workRequested)
        PropertyHolder.add(shortKey, workRequested)
    }


    @Then("work is generated")
    fun assertGenerated() {
        val shortKey = PropertyHolder.getActiveKey(AttoOpenBlock::class.java)!!
        val block = PropertyHolder.get(AttoOpenBlock::class.java, shortKey)
        val hash = block.hash
        val workGenerated = Waiter.waitUntilNonNull {
            pubSubTemplate.pullAndConvertAsync(
                CucumberConfiguration.workGeneratedSubscription,
                Integer.MAX_VALUE,
                true,
                WorkGenerated::class.java
            ).get()
                .map {
                    it.ack()
                    it.payload
                }.firstOrNull { it.hash == hash }
        }!!
        val workRequested = PropertyHolder.get(WorkRequested::class.java, shortKey)

        val expectedWorkGenerated = WorkGenerated(
            workRequested.callbackUrl,
            workRequested.hash,
            workRequested.threshold,
            workGenerated.work
        )
        assertEquals(expectedWorkGenerated, workGenerated)
        assertTrue(
            AttoWork.isValid(
                AttoNetwork.LOCAL,
                block.timestamp,
                block.hash,
                workGenerated.work
            )
        )
    }

}