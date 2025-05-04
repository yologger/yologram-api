package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.entity.BoardCommentCount
import link.yologram.api.domain.bms.exception.BoardCommentCountException
import link.yologram.api.domain.bms.repository.BoardCommentCountRepository
import link.yologram.api.global.model.APIEnvelop
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardCommentCountService(
    private val boardCommentCountRepository: BoardCommentCountRepository
) {
    @Transactional
    fun increaseCount(bid: Long): APIEnvelop<Long> {
        val boardCommentCount = boardCommentCountRepository.findByBid(bid).orElseGet {
            boardCommentCountRepository.save(BoardCommentCount(bid = bid, count = 0))
        }
        boardCommentCount.count += 1
        return APIEnvelop(data = boardCommentCount.count)
    }

    @Transactional
    fun decreaseCount(bid: Long) {
        val boardCommentCount = boardCommentCountRepository.findByBid(bid).orElseThrow { BoardCommentCountException("Board comment count not found") }
        boardCommentCount.count -= 1
        if (boardCommentCount.count == 0L)
            boardCommentCountRepository.delete(boardCommentCount)
    }
}