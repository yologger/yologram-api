package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.dto.comment.CommentData
import link.yologram.api.domain.bms.entity.Comment
import link.yologram.api.domain.bms.exception.BmsException
import link.yologram.api.domain.bms.exception.BoardCommentMismatchException
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.CommentNotFoundException
import link.yologram.api.domain.bms.repository.BoardRepository
import link.yologram.api.domain.bms.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository,
    private val boardCommentCountService: BoardCommentCountService
) {
    @Transactional
    fun createComment(boardId: Long, userId: Long, content: String): CommentData {
        val board = boardRepository.findById(boardId).orElseThrow { BoardNotFoundException("Board not found") }
        val comment = commentRepository.save(Comment(uid = userId, content = content, bid = board.id))
        boardCommentCountService.increaseCount(board.id)
        return CommentData.fromEntity(comment)
    }

    @Transactional
    fun deleteComment(boardId: Long, commentId: Long) {
        val board = boardRepository.findById(boardId).orElseThrow { BoardNotFoundException("Board not found") }
        val comment = commentRepository.findById(commentId).orElseThrow { CommentNotFoundException("Comment not found") }
        if (board.id != comment.bid) throw BoardCommentMismatchException("Comment does not belong to the board.")
        commentRepository.deleteById(comment.id)
        boardCommentCountService.decreaseCount(board.id)
    }

    @Transactional(readOnly = true)
    fun getCommentsByBid(bid: Long): List<CommentData> {
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board not found") }
        return commentRepository.findByBid(board.id).map { CommentData.fromEntity(it) }
    }
}