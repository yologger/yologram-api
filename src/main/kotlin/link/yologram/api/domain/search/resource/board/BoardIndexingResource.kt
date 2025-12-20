package link.yologram.api.domain.search.resource.board

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.search.service.board.BoardIndexingService
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.wrapOk
import org.opensearch.action.index.IndexResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "검색/게시글인덱싱", description = "게시글 인덱싱 엔드포인트 (search/BoardIndexingResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/search/v1/internal/indexing", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardIndexingResource(
    private val boardIndexingService: BoardIndexingService
) {
    private val logger = LoggerFactory.getLogger(BoardIndexingResource::class.java)

    @Operation(summary = "게시글 단건 인덱싱", description = "DB에서 bid로 게시글 단건 조회 후, Opensearch에 인덱싱한다.")
    @ApiResponse(
        responseCode = "200",
        description = "인덱싱 완료",
    )
    @PutMapping("/boards/{bid}")
    fun indexing(@PathVariable bid: Long): ResponseEntity<Unit> {
        boardIndexingService.index(bid)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "게시글 다건 범위 인덱싱", description = "DB에서 from ~ to 사이의 게시글을 조회하여, Opensearch에 인덱싱")
    @ApiResponse(
        responseCode = "202",
        description = "인덱싱 요청 완료",
    )
    @PutMapping("/boards/{from}/{to}")
    fun indexing(@PathVariable from: Long, @PathVariable to: Long): ResponseEntity<Unit> {
        boardIndexingService.index(from, to)
        return ResponseEntity.accepted().build()
    }

    @Operation(summary = "게시글 전체 인덱싱", description = "모든 게시글을 Opensearch에 인덱싱")
    @ApiResponse(
        responseCode = "200",
        description = "인덱싱 완료",
    )
    @PutMapping("/boards")
    fun fullIndexing(): ResponseEntity<Unit> {
        /**
         * id가 가장 큰 board를 database에서 조회하여 opensearch로 indexing.
         * 분산 데이터 그리드(Hazelcast)에 indexing 해야할 작업을 queueing.
         * TaskExecutor 같은 worker가 이를 가져와 청킹하여 bulkUpdate() 호출하여 upsert.
         */
         boardIndexingService.fullIndex()
        return ResponseEntity.accepted().build()
    }

    fun reindexing() {
        // 기존 index의 document를 새로운 index로 복사
        // mapping 변경, 새로운 analyzer 적용
        // shard, refresh interval 변경
        // ...
    }
}