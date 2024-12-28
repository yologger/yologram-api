package link.yologram.api.infrastructure.repository

import link.yologram.api.infrastructure.entity.Board
import link.yologram.api.infrastructure.enum.BoardStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BoardRepository: JpaRepository<Board, Long>, BoardCustomRepository {
    @Query("UPDATE Board b set b.status = :status WHERE b.uid = :uid")
    @Modifying
    fun updateBoardStatusByUid(uid: Long, status: BoardStatus): Int
}