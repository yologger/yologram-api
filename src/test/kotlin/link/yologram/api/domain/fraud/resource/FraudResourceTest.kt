package link.yologram.api.domain.fraud.resource

import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.fraud.exception.KisaWhoisException
import link.yologram.api.domain.fraud.exception.NetworkException
import link.yologram.api.domain.fraud.model.IpInfoResponse
import link.yologram.api.domain.fraud.service.IpDomainService
import link.yologram.api.global.model.APIEnvelop
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTestSupport(
    controllers = [FraudResource::class]
)
class FraudResourceTest(
    @Autowired var client: WebTestClient
) {
    @MockBean
    lateinit var ipDomainService: IpDomainService

    @Nested
    @DisplayName("IP 정보 조회")
    inner class GetIpInfoTest {

        @Test
        @DisplayName("IP 정보 조회 성공 시, 200을 반환한다")
        fun `IP 정보 조회 성공 시, 200을 반환한다`() {
            // Given
            val testIp = "211.234.125.1"
            val response = APIEnvelop(
                data = IpInfoResponse(
                    query = testIp,
                    queryType = "IPv4",
                    registry = "KRNIC",
                    countryCode = "KR",
                    range = "211.234.125.0 - 211.234.125.255",
                    prefix = "211.234.125.0/24",
                    netType = "ALLOCATED PORTABLE",
                    servName = "KT",
                    orgName = "케이티",
                    orgID = "ORG-KT-KR",
                    addr = "서울특별시 종로구 세종대로 55",
                    zipCode = "03142",
                    regDate = "20100101",
                    name = "홍길동",
                    phone = "02-1234-5678",
                    email = "admin@kt.com"
                )
            )

            given(ipDomainService.getIpInfo(testIp)).willReturn(response)

            // When & Then
            client.get()
                .uri { uriBuilder ->
                    uriBuilder.path(URI_IP_INFO)
                        .queryParam("ip", testIp)
                        .build()
                }
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.query").isEqualTo(testIp)
                .jsonPath("$.data.queryType").isEqualTo("IPv4")
                .jsonPath("$.data.countryCode").isEqualTo("KR")
                .jsonPath("$.data.servName").isEqualTo("KT")
                .jsonPath("$.data.orgName").isEqualTo("케이티")
                .jsonPath("$.data.email").isEqualTo("admin@kt.com")
        }

        @Test
        @DisplayName("IP 파라미터가 없을 때, 400을 반환한다")
        fun `IP 파라미터가 없을 때, 400을 반환한다`() {
            // When & Then
            client.get()
                .uri(URI_IP_INFO)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        @DisplayName("KisaWhoisException 발생 시, 400을 반환한다")
        fun `KisaWhoisException 발생 시, 400을 반환한다`() {
            // Given
            val testIp = "8.8.8"
            val errorMessage = "IPv4주소 형식이 올바르지 않습니다."

            given(ipDomainService.getIpInfo(testIp))
                .willThrow(KisaWhoisException(errorMessage))

            // When & Then
            client.get()
                .uri { uriBuilder ->
                    uriBuilder.path(URI_IP_INFO)
                        .queryParam("ip", testIp)
                        .build()
                }
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo(errorMessage)
        }
        @Test
        @DisplayName("NetworkException 발생 시, 500을 반환한다")
        fun `NetworkException 발생 시, 500을 반환한다`() {
            // Given
            val testIp = "8.8.8.8"
            val errorMessage = "Network error occurred"

            given(ipDomainService.getIpInfo(testIp))
                .willThrow(NetworkException(errorMessage))

            // When & Then
            client.get()
                .uri { uriBuilder ->
                    uriBuilder.path(URI_IP_INFO)
                        .queryParam("ip", testIp)
                        .build()
                }
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo(errorMessage)
        }


    }

    companion object {
        const val URI_IP_INFO = "/api/fraud/v1/admin/ip-info"
    }

}