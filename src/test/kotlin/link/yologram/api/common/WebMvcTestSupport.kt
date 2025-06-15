package link.yologram.api.common

import link.yologram.api.config.JwtConfig
import link.yologram.api.config.WebMvcConfig
import link.yologram.api.domain.ums.util.JwtUtil
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.context.annotation.Import
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@WebMvcTest
@Import(JwtUtil::class, JwtConfig::class, WebMvcConfig::class)
@AutoConfigureWebTestClient(timeout = "1500000")
annotation class WebMvcTestSupport(
    val controllers: Array<KClass<*>> = []
)