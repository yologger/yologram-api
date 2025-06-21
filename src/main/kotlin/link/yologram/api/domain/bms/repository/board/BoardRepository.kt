package link.yologram.api.domain.bms.repository.board

import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.domain.bms.enum.BoardStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BoardRepository: JpaRepository<Board, Long>, BoardCustomRepository {

    @Query("UPDATE Board b set b.status = :status WHERE b.uid = :uid")
    @Modifying(clearAutomatically = true)
    fun updateBoardStatusByUid(uid: Long, status: BoardStatus): Int
}