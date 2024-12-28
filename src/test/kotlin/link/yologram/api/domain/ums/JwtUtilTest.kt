package link.yologram.api.domain.ums

import link.yologram.api.config.JwtConfig
import link.yologram.api.domain.ums.exception.AuthException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest

@EnableConfigurationProperties
@SpringBootTest(
    classes = [
        JwtConfig::class,
        JwtUtil::class
    ]
)
class JwtUtilTest(
    @Autowired val jwtUtil: JwtUtil,
    @Autowired val jwtConfig: JwtConfig
) {
    @Test
    fun `JWT 생성 및 검증성공 테스트`() {
        val uid: Long = 1
        val jwtClaim = JwtClaim(uid = uid)
        val jwtToken = jwtUtil.createToken(jwtClaim)

        assertThat(jwtUtil.getTokenClaim(jwtToken).uid).isEqualTo(uid)
    }

    @Test
    fun `유효하지 않은 JWT 검증실패 테스트`() {
        val uid: Long = 1
        val jwtClaim = JwtClaim(uid = uid)
        val jwtToken = jwtUtil.createToken(jwtClaim) + "dummy"

        assertThatThrownBy {
            jwtUtil.validateToken(jwtToken)
        }.isExactlyInstanceOf(AuthException::class.java)
    }
}