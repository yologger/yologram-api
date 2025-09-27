package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.model.comment.CommentData
import link.yologram.api.domain.bms.entity.BoardComment
import link.yologram.api.domain.bms.exception.BoardCommentMismatchException
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.CommentNotFoundException
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.bms.repository.BoardCommentRepository
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopPage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardCommentService(
    private val boardRepository: BoardRepository,
    private val boardCommentRepository: BoardCommentRepository,
    private val boardCommentCountService: BoardCommentCountService
) {
    @Transactional
    fun createComment(boardId: Long, userId: Long, content: String): APIEnvelop<CommentData> {
        val board = boardRepository.findById(boardId).orElseThrow { BoardNotFoundException("Board not found") }
        val boardComment = boardCommentRepository.save(BoardComment(uid = userId, content = content, bid = board.id))
        boardCommentCountService.increaseCount(board.id)
        return APIEnvelop(data = CommentData.fromEntity(boardComment))
    }

    @Transactional
    fun deleteComment(boardId: Long, commentId: Long) {
        val board = boardRepository.findById(boardId).orElseThrow { BoardNotFoundException("Board not found") }
        val boardComment = boardCommentRepository.findById(commentId).orElseThrow { CommentNotFoundException("BoardComment not found") }
        if (board.id != boardComment.bid) throw BoardCommentMismatchException("BoardComment does not belong to the board.")
        boardCommentRepository.deleteById(boardComment.id)
        boardCommentCountService.decreaseCount(board.id)
    }

    @Transactional(readOnly = true)
    fun getCommentsByBid(bid: Long): APIEnvelopPage<CommentData> {
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board not found") }
        return APIEnvelopPage(data = boardCommentRepository.findAllByBid(board.id).map { CommentData.fromEntity(it) })
    }


}