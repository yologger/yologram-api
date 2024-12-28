package link.yologram.api.global

import link.yologram.api.domain.ums.service.UserService
import link.yologram.api.domain.ums.resource.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [
    UserResource::class
])
@AutoConfigureWebTestClient(timeout = "1500000")
abstract class AbstractWebMvcTest {

    @MockBean
    lateinit var userService: UserService

    @Autowired
    lateinit var client: WebTestClient
}