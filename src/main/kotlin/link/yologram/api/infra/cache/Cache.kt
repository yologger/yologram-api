package link.yologram.api.infra.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.ums.model.UserData
import java.time.Duration

data class Cache<V>(
    val key: String,
    val type: TypeReference<V>,
    val duration: Duration,
) {
    companion object Factory {

        private const val USER_PREFIX = "ums:users"
        private const val BOARD_PREFIX = "bms:boards"

        fun user(uid: Long) = Cache<UserData> (
            key = "$USER_PREFIX:v1:user:$uid",
            type = jacksonTypeRef(),
            duration = Duration.ofMinutes(10)
        )

        fun board(bid: Long) = Cache<BoardData> (
            key = "$BOARD_PREFIX:v1:board:$bid",
            type = jacksonTypeRef(),
            duration = Duration.ofMinutes(10)
        )

        fun boardWithMetrics(bid: Long) = Cache<BoardDataWithMetrics> (
            key = "$BOARD_PREFIX:v1:board-with-metrics:$bid",
            type = jacksonTypeRef(),
            duration = Duration.ofMinutes(10)
        )

        fun userRecentBoards(uid: Long) = Cache<List<BoardDataWithMetrics>>(
            key = "$BOARD_PREFIX:$uid:recent-boards:v1",
            type = jacksonTypeRef(),
            duration = Duration.ofHours(6)
        )
    }
}