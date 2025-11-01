package link.yologram.api.infra.cache

import link.yologram.api.config.RedisConfig
import link.yologram.api.domain.bms.model.board.BoardData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        RedisConfig::class,
        RedisCacheService::class,
        RedisAutoConfiguration::class,
        JacksonAutoConfiguration::class,
    ]
)
@Testcontainers
class RedisCacheServiceTest() {

    @Autowired
    lateinit var cacheService: RedisCacheService

    companion object {
        @Container
        @JvmStatic
        val container = GenericContainer("redis:7.0.8-alpine")
            .withExposedPorts(6379)

        @BeforeAll
        @JvmStatic
        fun setup() {
            // Redis 컨테이너가 사용하는 host와 port를 시스템 프로퍼티로 설정
            System.setProperty("spring.data.redis.host", container.host)
            System.setProperty("spring.data.redis.port", container.getMappedPort(6379).toString())
        }
    }

    @Test
    fun `게시글 정보 단건 저장, 조회`() {
        val boardData = BoardData(bid = 10, uid = 1, title = "title10", content = "content10", createdDate = LocalDateTime.now(), modifiedDate = LocalDateTime.now())
        cacheService.set(cache = Cache.board(boardData.bid), value = boardData)
        val queried = cacheService.getOrNull(cache = Cache.board(boardData.bid))
        assertThat(queried).isNotNull
        assertThat(queried?.title).isEqualTo(boardData.title)
    }
}