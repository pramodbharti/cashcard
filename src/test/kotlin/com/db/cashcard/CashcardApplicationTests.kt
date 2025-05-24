package com.db.cashcard

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardApplicationTests {

    @Autowired
    val restTemplate: TestRestTemplate? = null

    @Test
    fun shouldReturnACashCardWhenDataIsSaved() {
        val response = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity("/cashcards/99", String::class.java)
        assertThat(response?.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response?.body)
        val id: Number = documentContext.read("$.id")
        assertThat(id).isEqualTo(99)
        val amount: Double = documentContext.read("$.amount")
        assertThat(amount).isEqualTo(123.45)
    }

    @Test
    @DirtiesContext
    fun shouldCreateANewCashCard() {
        val cashCard = CashCard(null, 250.0, "sarah1")
        val createResponse = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.postForEntity("/cashcards", cashCard, Void::class.java)
        assertThat(createResponse?.statusCode).isEqualTo(HttpStatus.CREATED)

        val locationOfNewCashCard = createResponse?.headers?.location
        val getResponse = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity(locationOfNewCashCard, String::class.java)
        assertThat(getResponse?.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext: DocumentContext = JsonPath.parse(getResponse?.body)
        val id: Number = documentContext.read("$.id")
        assertThat(id).isNotNull
        val amount: Double = documentContext.read("$.amount")
        assertThat(amount).isEqualTo(250.00)
    }

    @Test
    fun shouldNotReturnACashCardWithAnUnknownId() {
        val response = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity("/cashcards/1000", String::class.java)
        assertThat(response?.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response?.body).isBlank()
    }

    @Test
    fun shouldReturnAllCashCardsWhenListIsRequested() {
        val getResponse = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity("/cashcards", String::class.java)
        assertThat(getResponse?.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(getResponse!!.getBody())
        val cashCardCount = documentContext.read<Int>("$.length()")
        assertThat(cashCardCount).isEqualTo(3)

        val ids: JSONArray? = documentContext.read<JSONArray?>("$..id")
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101)

        val amounts: JSONArray? = documentContext.read<JSONArray?>("$..amount")
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00)
    }

    @Test
    fun shouldReturnAPageOfCashCards() {
        val response = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity("/cashcards?page=0&size=1", String::class.java)
        assertThat(response?.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response?.getBody())
        val page = documentContext.read<JSONArray>("$[*]")
        assertThat(page.size).isEqualTo(1)
    }

    @Test
    fun shouldReturnASortedPageOfCashCards() {
        val getResponse = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String::class.java)
        assertThat(getResponse?.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(getResponse?.body)

        val read: JSONArray = documentContext.read("$[*]")
        assertThat(read.size).isEqualTo(1)

        val amount: Double = documentContext.read("$[0].amount")
        assertThat(amount).isEqualTo(150.00)
    }

    @Test
    fun shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        val response = restTemplate
            ?.withBasicAuth("sarah1", "abc123")
            ?.getForEntity("/cashcards", String::class.java)
        assertThat(response?.statusCode).isEqualTo(HttpStatus.OK)

        val documentContext = JsonPath.parse(response?.getBody())
        val page = documentContext.read<JSONArray>("$[*]")
        assertThat(page.size).isEqualTo(3)

        val amounts = documentContext.read<JSONArray>("$..amount")
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00)
    }

    @Test
    fun shouldNotReturnACashCardWhenUsingBadCredentials() {
        var response = restTemplate
            ?.withBasicAuth("BAD-USER", "abc123")
            ?.getForEntity("/cashcards/99", String::class.java)
        assertThat(response?.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)

        response = restTemplate!!
            .withBasicAuth("sarah1", "BAD-PASSWORD")
            .getForEntity("/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun shouldRejectUsersWhoAreNotCardOwners() {
        val response = restTemplate
            ?.withBasicAuth("hank-owns-no-cards", "qrs456")
            ?.getForEntity("/cashcards/99", String::class.java)
        assertThat(response?.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun contextLoads() {
    }

}
