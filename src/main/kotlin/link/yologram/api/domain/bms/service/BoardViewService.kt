package link.yologram.api.domain.bms.service

import jakarta.transaction.Transactional
import link.yologram.api.domain.bms.entity.BoardViewCount
import link.yologram.api.domain.bms.entity.BoardViewEvent
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.bms.repository.BoardViewCountRepository
import link.yologram.api.domain.bms.repository.BoardViewEventRepository
import link.yologram.api.domain.ums.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class BoardViewService(
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val boardViewEventRepository: BoardViewEventRepository,
    private val boardViewCountRepository: BoardViewCountRepository
) {
    @Transactional
    fun recordView(boardId: Long, uid: Long?, ip: String?) {
        uid?.let { userId: Long -> if (!userRepository.existsById(userId)) throw UserNotFoundException("User not found") }
        if (!boardRepository.existsById(boardId)) throw BoardNotFoundException("Board not found")

        // 중복 체크
        val exists = boardViewEventRepository.existsByBidAndUidAndIp(bid = boardId, uid = uid, ip = ip)

        if (!exists) {
            // event 저장
            boardViewEventRepository.save(
                BoardViewEvent(bid = boardId, uid = uid, ip = ip)
            )

            // board view count 증가
            val viewCount = boardViewCountRepository.findById(boardId)
                .orElse(BoardViewCount(bid = boardId, count = 0))

            viewCount.count += 1
            boardViewCountRepository.save(viewCount)
        }
    }
}