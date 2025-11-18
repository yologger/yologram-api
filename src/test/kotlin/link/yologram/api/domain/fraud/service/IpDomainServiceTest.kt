package link.yologram.api.domain.fraud.service

import link.yologram.api.domain.fraud.exception.KisaWhoisException
import link.yologram.api.domain.fraud.exception.NetworkException
import link.yologram.api.infra.api.datagokr.kisa.client.KisaWhoisClient
import link.yologram.api.infra.api.datagokr.kisa.client.dto.KisaWhoisIpInfoResponse
import link.yologram.api.infra.api.datagokr.kisa.exception.KisaWhoisIpInfoFailureException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.junit.jupiter.api.assertThrows

class IpDomainServiceTest {

    private lateinit var kisaWhoisClient: KisaWhoisClient
    private lateinit var ipDomainService: IpDomainService

    @BeforeEach
    fun setUp() {
        kisaWhoisClient = mock(KisaWhoisClient::class.java)
        ipDomainService = IpDomainService(kisaWhoisClient)
    }

    @Test  // 이제 JUnit 5의 @Test
    @DisplayName("IP 정보 조회 성공")
    fun `getIpInfo 성공 테스트`() {
        // Given
        val testIp = "211.234.125.1"

        val mockWhoisResponse = KisaWhoisIpInfoResponse.Response.WhoisResponse(
            query = testIp,
            queryType = "IPv4",
            registry = "KRNIC",
            countryCode = "KR",
            korean = KisaWhoisIpInfoResponse.Response.WhoisResponse.IpResponse(
                PI = KisaWhoisIpInfoResponse.Response.WhoisResponse.IpResponse.IpDetailResponse(
                    netinfo = KisaWhoisIpInfoResponse.Response.WhoisResponse.IpResponse.IpDetailResponse.NetInfo(
                        range = "211.234.125.0 - 211.234.125.255",
                        prefix = "211.234.125.0/24",
                        netType = "ALLOCATED PORTABLE",
                        servName = "KT",
                        orgName = "케이티",
                        orgID = "ORG-KT-KR",
                        addr = "서울특별시 종로구 세종대로 55",
                        zipCode = "03142",
                        regDate = "20100101"
                    ),
                    techContact = KisaWhoisIpInfoResponse.Response.WhoisResponse.IpResponse.IpDetailResponse.TechContact(
                        name = "홍길동",
                        phone = "02-1234-5678",
                        email = "admin@kt.com"
                    )
                ),
                user = null
            ),
            english = null,
            error = null
        )

        val mockResponse = KisaWhoisIpInfoResponse.Response(
            result = KisaWhoisIpInfoResponse.Response.ResultResponse(
                resultCode = "001",
                resultMessage = "요청이 정상적으로 처리되었습니다."
            ),
            whois = mockWhoisResponse
        )

        given(kisaWhoisClient.getIpInfo(testIp))
            .willReturn(Result.success(mockResponse))

        // When
        val result = ipDomainService.getIpInfo(testIp)

        // Then
        assertNotNull(result)
        assertNotNull(result.data)

        with(result.data!!) {
            assertEquals(testIp, query)
            assertEquals("IPv4", queryType)
            assertEquals("KRNIC", registry)
            assertEquals("KR", countryCode)
            assertEquals("211.234.125.0 - 211.234.125.255", range)
            assertEquals("211.234.125.0/24", prefix)
            assertEquals("ALLOCATED PORTABLE", netType)
            assertEquals("KT", servName)
            assertEquals("케이티", orgName)
            assertEquals("ORG-KT-KR", orgID)
            assertEquals("서울특별시 종로구 세종대로 55", addr)
            assertEquals("03142", zipCode)
            assertEquals("20100101", regDate)
            assertEquals("홍길동", name)
            assertEquals("02-1234-5678", phone)
            assertEquals("admin@kt.com", email)
        }
    }

    @Test
    @DisplayName("KISA Whois API 에러 - KisaWhoisException 발생")
    fun `KisaWhoisIpInfoFailureException 발생 시 KisaWhoisException으로 변환`() {
        // Given
        val testIp = "8.8.8"
        val errorMessage = "IPv4주소 형식이 올바르지 않습니다."

        given(kisaWhoisClient.getIpInfo(testIp))
            .willReturn(Result.failure(KisaWhoisIpInfoFailureException(errorMessage)))

        // When & Then
        val exception = assertThrows<KisaWhoisException> {
            ipDomainService.getIpInfo(testIp)
        }

        assertEquals(errorMessage, exception.message)
    }

    @Test
    @DisplayName("네트워크 에러 - NetworkException 발생")
    fun `NetworkException 발생 시 NetworkException으로 재전달`() {
        // Given
        val testIp = "8.8.8.8"
        val errorMessage = "Connection timeout"

        given(kisaWhoisClient.getIpInfo(testIp))
            .willReturn(Result.failure(NetworkException(errorMessage)))

        // When & Then
        val exception = assertThrows<NetworkException> {
            ipDomainService.getIpInfo(testIp)
        }

        assertEquals(errorMessage, exception.message)
    }
}