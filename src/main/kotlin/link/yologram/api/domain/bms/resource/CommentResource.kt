package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "게시글/댓글", description = "게시글/댓글 엔드포인트 (bms/CommentResource)")
@RestController
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class CommentResource {
}