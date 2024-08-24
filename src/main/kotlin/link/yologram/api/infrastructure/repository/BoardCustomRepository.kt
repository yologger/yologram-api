package link.yologram.api.infrastructure.repository

import link.yologram.api.infrastructure.entity.Board

interface BoardCustomRepository {
    fun findBoardsByUidOrderByCreateDateDesc(uid: Long, page: Long, size: Long): List<Board>
}