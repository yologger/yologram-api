package link.yologram.api.domain.bms.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.domain.bms.entity.QBoard
import link.yologram.api.domain.bms.entity.QBoardCommentCount
import org.springframework.transaction.support.TransactionTemplate

interface BoardCustomRepository {
    fun findOneById(id: Long): BoardDataWithMetrics?
    fun findBoards(cursorId: Long?, pageSize: Long): List<BoardData>
    fun findBoardsWithMetrics(cursorId: Long?, pageSize: Long): List<BoardDataWithMetrics>
    fun findBoardsByUidOrderByCreateDateDesc(uid: Long, page: Long, size: Long): List<Board>
}

class BoardCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val template: TransactionTemplate
): BoardCustomRepository {

    private val qBoard = QBoard.board
    private val qBoardCommentCount = QBoardCommentCount.boardCommentCount

    override fun findOneById(id: Long): BoardDataWithMetrics? {

        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardDataWithMetrics::class.java,
                    qBoard.id,
                    qBoard.uid,
                    qBoard.title,
                    qBoard.body,
                    qBoard.createdDate,
                    qBoard.modifiedDate,
                    Projections.constructor(
                        BoardDataWithMetrics.Metrics::class.java,
                        qBoardCommentCount.count.coalesce(0L)
                    )
                )
            )
            .from(qBoard)
            .leftJoin(qBoardCommentCount)
            .on(qBoard.id.eq(qBoardCommentCount.bid))
            .where(qBoard.id.eq(id))
            .fetchOne()
    }

    override fun findBoards(cursorId: Long?, pageSize: Long): List<BoardData> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardData::class.java,
                    qBoard.id,
                    qBoard.uid,
                    qBoard.title,
                    qBoard.body,
                    qBoard.createdDate,
                    qBoard.modifiedDate
                )
            )
            .from(qBoard)
            .where(cursorId?.let { qBoard.id.lt(it) })
            .orderBy(qBoard.id.desc())
            .limit(pageSize)
            .fetch()
    }

    override fun findBoardsWithMetrics(cursorId: Long?, pageSize: Long): List<BoardDataWithMetrics> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardDataWithMetrics::class.java,
                    qBoard.id,
                    qBoard.uid,
                    qBoard.title,
                    qBoard.body,
                    qBoard.createdDate,
                    qBoard.modifiedDate,
                    Projections.constructor(
                        BoardDataWithMetrics.Metrics::class.java,
                        qBoardCommentCount.count.coalesce(0L)
                    )
                )
            )
            .from(qBoard)
            .leftJoin(qBoardCommentCount)
            .on(qBoard.id.eq(qBoardCommentCount.bid))
            .where(cursorId?.let { qBoard.id.lt(it) })
            .orderBy(qBoard.id.desc())
            .limit(pageSize)
            .fetch()
    }

    override fun findBoardsByUidOrderByCreateDateDesc(uid: Long, page: Long, size: Long): List<Board> {
        return template.execute<List<Board>> {
            jpaQueryFactory.selectFrom(qBoard)
                .where(qBoard.uid.eq(uid))
                .offset(page * size)
                .limit(size)
                .orderBy(qBoard.id.desc())
                .fetch()
        } ?: emptyList()
    }
}