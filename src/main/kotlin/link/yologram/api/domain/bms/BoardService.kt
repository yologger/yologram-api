package link.yologram.api.domain.bms

import link.yologram.api.domain.bms.dto.BoardData
import link.yologram.api.domain.bms.dto.DeleteBoardResponse
import link.yologram.api.domain.bms.dto.GetBoardsByUidResponse
import link.yologram.api.domain.bms.dto.GetBoardsResponse
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.WrongBoardWriterException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.infrastructure.entity.Board
import link.yologram.api.infrastructure.repository.BoardRepository
import link.yologram.api.infrastructure.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    @Throws(UserNotFoundException::class)
    fun createBoard(uid: Long, title: String, body: String): BoardData {
        if (!userRepository.existsById(uid)) throw UserNotFoundException("User not found")
        val saved = boardRepository.save(
            Board(
                uid = uid,
                title = title,
                body = body
            )
        )
        return BoardData.fromEntity(board = saved)
    }

    @Transactional
    @Throws(UserNotFoundException::class, BoardNotFoundException::class, WrongBoardWriterException::class)
    fun editBoard(uid: Long, bid: Long, newTitle: String, newBody: String): BoardData {
        val board = validateBoard(bid, uid)
        board.get().title = newTitle
        board.get().body = newBody
        return BoardData.fromEntity(board.get())
    }

    @Transactional
    @Throws(UserNotFoundException::class, BoardNotFoundException::class, WrongBoardWriterException::class)
    fun deleteBoard(uid: Long, bid: Long): DeleteBoardResponse {
        validateBoard(bid, uid)
        boardRepository.deleteById(bid)
        return DeleteBoardResponse(uid = uid, bid = bid)
    }

    @Transactional(readOnly = true)
    fun getBoard(bid: Long): BoardData = BoardData.fromEntity(boardRepository.findById(bid).orElseThrow { BoardNotFoundException("Board not found") }!!)

    @Transactional(readOnly = true)
    fun getBoards(page: Int, size: Int): GetBoardsResponse {
        val boards = boardRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending())).map { BoardData.fromEntity(it) }.content
        return GetBoardsResponse(size = boards.size, boards = boards)
    }

    @Transactional(readOnly = true)
    fun getBoardsByUid(uid: Long, page: Long, size: Long): GetBoardsByUidResponse {
        val boards = boardRepository.findBoardsByUidOrderByCreateDateDesc(uid = uid, page = page, size = size).map { BoardData.fromEntity(it) }
        return GetBoardsByUidResponse(size = boards.size, boards = boards)
    }

    @Throws(UserNotFoundException::class, BoardNotFoundException::class, WrongBoardWriterException::class)
    private fun validateBoard(bid: Long, uid: Long): Optional<Board> {
        if (!userRepository.existsById(uid)) throw UserNotFoundException("User not found")
        val board = boardRepository.findById(bid)
        if (board.isEmpty) throw BoardNotFoundException("Board not found")
        if (board.get().uid != uid) throw WrongBoardWriterException("Wrong board writer")
        return board;
    }

}