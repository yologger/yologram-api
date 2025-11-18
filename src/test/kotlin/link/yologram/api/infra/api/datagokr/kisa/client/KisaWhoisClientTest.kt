package link.yologram.api.infra.api.datagokr.kisa.client

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import link.yologram.api.infra.api.datagokr.DataGoKrProperties
import link.yologram.api.infra.api.datagokr.kisa.exception.KisaWhoisIpInfoFailureException
import link.yologram.api.infra.api.datagokr.kisa.exception.NetworkException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.Test

class KisaWhoisClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var kisaWhoisClient: KisaWhoisClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()

        val dataGoKrProperties = DataGoKrProperties(
            url = mockWebServer.url("/").toString(),
            serviceKey = "test-service-key",
            connTimeout = 10,
            readTimeout = 10,
            writeTimeout = 10
        )

        kisaWhoisClient = KisaWhoisClientImpl(webClient, dataGoKrProperties)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    @DisplayName("IP 정보 조회 성공")
    fun `getIpInfo 성공 테스트`() {
        // Given
        val testIp = "8.8.8.8"
        val mockResponse = """
            {
              "response": {
                "result": {
                  "result_code": "10000",
                  "result_msg": "정상 응답 입니다."
                },
                "whois": {
                  "query": "8.8.8.8",
                  "queryType": "IPv4",
                  "registry": "ARIN",
                  "countryCode": "US"
                }
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse)
        )

        // When
        val result = kisaWhoisClient.getIpInfo(testIp)

        // Then
        assertTrue(result.isSuccess)
        val response = result.getOrThrow()
        assertEquals("8.8.8.8", response.whois.query)
        assertEquals("US", response.whois.countryCode)
        assertNull(response.whois.error)

        // 요청 검증
        val recordedRequest = mockWebServer.takeRequest()
        assertTrue(recordedRequest.path!!.contains("/B551505/whois/ip_address"))
        assertTrue(recordedRequest.path!!.contains("query=$testIp"))
        assertTrue(recordedRequest.path!!.contains("serviceKey=test-service-key"))
        assertTrue(recordedRequest.path!!.contains("answer=json"))
    }

    @Test
    @DisplayName("ip가 유효하지 않을 때 실패처리")
    fun `API 에러 응답 테스트`() {
        // Given
        val testIp = "8.8.8"
        val mockResponse = """
            {
              "response": {
                "result": {
                  "result_code": "041",
                  "result_msg": "IPv4주소 형식이 올바르지 않습니다."
                },
                "whois": {
                  "error": {
                    "query": "8.8.8",
                    "error_code": "041",
                    "error_msg": "IPv4주소 형식이 올바르지 않습니다."
                  }
                }
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse)
        )

        // When
        val result = kisaWhoisClient.getIpInfo(testIp)

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(KisaWhoisIpInfoFailureException::class.java, result.exceptionOrNull())
    }

    @Test
    @DisplayName("네트워크 에러 처리")
    fun `네트워크 에러 테스트`() {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When
        val result = kisaWhoisClient.getIpInfo("8.8.8.8")

        // Then
        assertTrue(result.isFailure)
        assertInstanceOf(NetworkException::class.java, result.exceptionOrNull())
    }

    @Test
    @DisplayName("타임아웃 처리")
    fun `타임아웃 테스트`() {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeadersDelay(10, java.util.concurrent.TimeUnit.SECONDS)
        )

        // When
        val result = kisaWhoisClient.getIpInfo("8.8.8.8")

        // Then
        assertTrue(result.isFailure)
    }
}