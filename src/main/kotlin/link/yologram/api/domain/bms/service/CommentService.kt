package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.dto.comment.CommentData
import link.yologram.api.domain.bms.entity.Comment
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.repository.BoardRepository
import link.yologram.api.domain.bms.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository
) {
    @Transactional
    fun createComment(bid: Long, uid: Long, content: String): Long {
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board not found") }
        val comment = commentRepository.save(Comment(uid = uid, content = content, bid = bid))
        return comment.id
    }

    @Transactional
    fun getCommentsByBid(bid: Long): List<CommentData> {
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board not found") }
        return commentRepository.findByBid(board.id).map { CommentData.fromEntity(it) }
    }
}