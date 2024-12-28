package link.yologram.api.domain.auth.resolver

import link.yologram.api.domain.auth.dto.AuthData
import link.yologram.api.domain.auth.isAuthTokenHeader
import link.yologram.api.domain.auth.JwtUtil
import link.yologram.api.domain.auth.exception.AuthException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AuthenticatedUserResolver(
    private val jwtUtil: JwtUtil
): HandlerMethodArgumentResolver {

    val logger: Logger = LoggerFactory.getLogger(AuthenticatedUserResolver::class.java)

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val accessToken = webRequest.headerNames.asSequence()
            .filter { headerKey -> isAuthTokenHeader(headerKey) }
            ?.firstOrNull()
            ?.takeIf {it.isNotBlank()}
            ?.let { webRequest.getHeader(it) }
            ?: throw AuthException("Empty auth header")

        val uid = jwtUtil.getTokenClaim(accessToken).uid
        return AuthData(uid = uid, accessToken = accessToken)
    }
}