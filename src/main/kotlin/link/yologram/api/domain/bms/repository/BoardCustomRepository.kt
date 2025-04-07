package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.Board

interface BoardCustomRepository {
    fun findBoardsByUidOrderByCreateDateDesc(uid: Long, page: Long, size: Long): List<Board>
}