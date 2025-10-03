package link.yologram.api.domain.ums.resolver

import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.exception.AuthHeaderEmptyException
import link.yologram.api.domain.ums.extension.isAuthTokenHeader
import link.yologram.api.domain.ums.util.JwtUtil
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
        val isParameterOptional = parameter.isOptional

        try {
            val accessToken = webRequest.headerNames.asSequence()
                .filter { headerKey -> isAuthTokenHeader(headerKey) }
                .firstOrNull()
                ?.let { webRequest.getHeader(it) }
                ?: throw AuthHeaderEmptyException("Empty auth header")

            val uid = jwtUtil.getTokenClaim(accessToken).uid
            return AuthData(uid = uid, accessToken = accessToken)

        } catch (exception: Exception) {
            logger.warn(exception.message)
            if (isParameterOptional) {
                return null
            } else {
                throw exception
            }
        }
    }
}