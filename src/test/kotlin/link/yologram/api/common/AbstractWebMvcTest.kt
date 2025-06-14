package link.yologram.api.common

import link.yologram.api.config.JwtConfig
import link.yologram.api.config.WebMvcConfig
import link.yologram.api.domain.ums.resource.UserResource
import link.yologram.api.domain.ums.util.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [
    UserResource::class
])
@Import(JwtUtil::class, JwtConfig::class, WebMvcConfig::class)
@AutoConfigureWebTestClient(timeout = "1500000")
abstract class AbstractWebMvcTest {

    @Autowired
    lateinit var client: WebTestClient
}