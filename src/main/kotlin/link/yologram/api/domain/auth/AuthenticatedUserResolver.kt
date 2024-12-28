package link.yologram.api.domain.auth

import link.yologram.api.domain.auth.dto.AuthData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

//class AuthenticatedUserResolver(
//
//): HandlerMethodArgumentResolver {
//
//    val logger: Logger = LoggerFactory.getLogger(AuthenticatedUserResolver::class.java)
//
//    override fun supportsParameter(parameter: MethodParameter): Boolean {
//        return parameter.parameterType == AuthData::class.java
//    }
//
//    override fun resolveArgument(
//        parameter: MethodParameter,
//        mavContainer: ModelAndViewContainer?,
//        webRequest: NativeWebRequest,
//        binderFactory: WebDataBinderFactory?
//    ): Any? {
//        webRequest.getHeader(AuthData.USER_KEY) ?: ""
//
//
//    }
//}