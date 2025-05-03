package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.entity.BoardCommentCount
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.repository.BoardCommentCountRepository
import link.yologram.api.domain.bms.repository.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardCommentCountService(
    private val boardRepository: BoardRepository,
    private val boardCommentCountRepository: BoardCommentCountRepository
) {
    @Transactional
    fun increaseCount(bid: Long): Long {
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board not found") }
        val boardCommentCount = boardCommentCountRepository.findByBid(board.id).orElseGet {
            boardCommentCountRepository.save(BoardCommentCount(bid = board.id, count = 0))
        }
        boardCommentCount.count += 1
        return boardCommentCount.count
    }

    @Transactional
    fun decreaseCount(bid: Long) {

    }
}