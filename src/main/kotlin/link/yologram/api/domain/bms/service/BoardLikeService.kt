package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.model.LikeBoardResponse
import link.yologram.api.domain.bms.model.UnlikeBoardResponse
import link.yologram.api.domain.bms.entity.BoardLike
import link.yologram.api.domain.bms.entity.BoardLikeCount
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.UserAlreadyLikeBoard
import link.yologram.api.domain.bms.exception.UserNotLikeBoardException
import link.yologram.api.domain.bms.repository.BoardLikeCountRepository
import link.yologram.api.domain.bms.repository.BoardLikeRepository
import link.yologram.api.domain.bms.repository.BoardRepository
import link.yologram.api.global.model.APIEnvelop
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardLikeService(
    private val boardRepository: BoardRepository,
    private val boardLikeRepository: BoardLikeRepository,
    private val boardLikeCountRepository: BoardLikeCountRepository,
) {

    @Transactional
    fun likeBoard(uid: Long, bid: Long): APIEnvelop<LikeBoardResponse> {
        // 게시글 조회
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board Not Found") }

        // 사용자-게시글 좋아요 정보 조회 (없으면 새로 생성)
        val like = boardLikeRepository.findByUidAndBid(uid, bid).orElseGet { BoardLike(uid = uid, bid = bid) }

        // 좋아요를 누른 적이 없으면
        if (like.id == 0L) {
            // 새로 좋아요를 저장
            boardLikeRepository.save(like)

            // 좋아요 카운트 조회 후 증가 또는 새로 저장
            val likeCount = boardLikeCountRepository.findByBid(board.id).orElseGet {
                BoardLikeCount(bid = board.id, count = 0)
            }
            likeCount.count++

            // 좋아요 카운트 저장
            boardLikeCountRepository.save(likeCount)

            // 응답 반환
            return APIEnvelop(data = LikeBoardResponse(uid = like.uid, bid = like.bid))
        } else {
            // 이미 좋아요를 누른 경우 예외 처리
            throw UserAlreadyLikeBoard("User $uid already liked board $bid")
        }
    }


    @Transactional
    fun unlikeBoard(uid: Long, bid: Long): APIEnvelop<UnlikeBoardResponse> {
        val board = boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board Not Found") }
        val like =  boardLikeRepository.findByUidAndBid(uid = uid, bid = board.id).orElseThrow { UserNotLikeBoardException("User $uid not like board $bid") }
        val boardLikeCount = boardLikeCountRepository.findByBid(bid = bid).orElseThrow { UserNotLikeBoardException("User $uid not like board $bid") }
        boardLikeRepository.delete(like)
        boardLikeCount.count += 1
        return APIEnvelop(data = UnlikeBoardResponse(uid = like.uid, bid = like.bid))
    }
}