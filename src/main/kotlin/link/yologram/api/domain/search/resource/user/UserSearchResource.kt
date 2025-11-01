package link.yologram.api.domain.search.resource.user

import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//@Tag(name = "검색/유저", description = "유저 검색 엔드포인트 (search/UserSearchResource)")
//@RestController
//@ApiResponseInvalidArgument
//@RequestMapping("/api/search/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
//class UserSearchResource {
//
//    private val logger = LoggerFactory.getLogger(UserSearchResource::class.java)
//
//    fun getUser() {
//
//    }
//}