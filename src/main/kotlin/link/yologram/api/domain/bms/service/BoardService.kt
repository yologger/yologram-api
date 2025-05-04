package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.dto.board.BoardData
import link.yologram.api.domain.bms.dto.DeleteBoardResponse
import link.yologram.api.domain.bms.dto.GetBoardsByUidResponse
import link.yologram.api.domain.bms.dto.GetBoardsResponse
import link.yologram.api.domain.bms.dto.board.BoardDataWithMetrics
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.BoardWrongWriterException
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.domain.bms.repository.BoardRepository
import link.yologram.api.domain.ums.repository.UserRepository
import link.yologram.api.global.model.APIEnvelopNextCursorPage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(rollbackFor = [Exception::class])
    @Throws(UserNotFoundException::class)
    fun createBoard(uid: Long, title: String, body: String): BoardData {
        if (!userRepository.existsById(uid)) throw UserNotFoundException("User not exist")
        val saved = boardRepository.save(
            Board(
                uid = uid,
                title = title,
                body = body
            )
        )
        return BoardData.fromEntity(board = saved)
    }

    @Transactional(rollbackFor = [Exception::class])
    @Throws(UserNotFoundException::class, BoardNotFoundException::class, BoardWrongWriterException::class)
    fun editBoard(uid: Long, bid: Long, newTitle: String, newBody: String): BoardData {
        val board = validateBoard(bid, uid)
        board.get().title = newTitle
        board.get().body = newBody
        return BoardData.fromEntity(board.get())
    }

    @Transactional(rollbackFor = [Exception::class])
    @Throws(UserNotFoundException::class, BoardNotFoundException::class, BoardWrongWriterException::class)
    fun deleteBoard(uid: Long, bid: Long): DeleteBoardResponse {
        validateBoard(bid, uid)
        boardRepository.deleteById(bid)
        return DeleteBoardResponse(uid = uid, bid = bid)
    }

    @Transactional(readOnly = true, rollbackFor = [Exception::class])
    fun getBoard(bid: Long): BoardDataWithMetrics? = boardRepository.findOneById(bid)

    @Transactional(readOnly = true, rollbackFor = [Exception::class])
    fun getBoardsWithMetricsV2(nextCursorId: String?, size: Long): APIEnvelopNextCursorPage<BoardDataWithMetrics> {
        val decodedNextCursorId = CursorUtil.decode(nextCursorId)
        val boards = boardRepository.findBoardsWithMetrics(decodedNextCursorId, size)

        val nextCursorId = boards.lastOrNull()?.bid
        val encodedNextCursor = nextCursorId?.let { CursorUtil.encode(it) }
        return APIEnvelopNextCursorPage(nextCursor = encodedNextCursor, data = boards)
    }

    @Transactional(readOnly = true, rollbackFor = [Exception::class])
    fun getBoardsByUid(uid: Long, page: Long, size: Long): GetBoardsByUidResponse {
        val boards = boardRepository.findBoardsByUidOrderByCreateDateDesc(uid = uid, page = page, size = size).map { BoardData.fromEntity(it) }
        return GetBoardsByUidResponse(size = boards.size, boards = boards)
    }

    @Throws(UserNotFoundException::class, BoardNotFoundException::class, BoardWrongWriterException::class)
    private fun validateBoard(bid: Long, uid: Long): Optional<Board> {
        if (!userRepository.existsById(uid)) throw UserNotFoundException("User not found")
        val board = boardRepository.findById(bid)
        if (board.isEmpty) throw BoardNotFoundException("Board not found")
        if (board.get().uid != uid) throw BoardWrongWriterException("Wrong board writer")
        return board;
    }

    class CursorUtil {

        companion object {
            private const val CURSOR_PREFIX = "cursor_prefix:"

            fun encode(id: Long): String {
                val raw = "$CURSOR_PREFIX$id"
                return Base64.getUrlEncoder().encodeToString(raw.toByteArray(Charsets.UTF_8))
            }

            fun decode(cursor: String?): Long? {
                if (cursor.isNullOrBlank()) return null
                return try {
                    val decoded = String(Base64.getUrlDecoder().decode(cursor), Charsets.UTF_8)
                    if (!decoded.startsWith(CURSOR_PREFIX)) null
                    else decoded.removePrefix(CURSOR_PREFIX).toLongOrNull()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}