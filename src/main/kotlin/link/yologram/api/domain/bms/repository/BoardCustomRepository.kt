package link.yologram.api.domain.bms.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.bms.entity.QBoard
import link.yologram.api.domain.bms.entity.QBoardCommentCount

interface BoardCustomRepository {
    fun findOneById(id: Long): BoardDataWithMetrics?
    fun findBoards(cursorId: Long?, pageSize: Long): List<BoardData>
    fun findBoardsWithMetrics(cursorId: Long?, pageSize: Long): List<BoardDataWithMetrics>
    fun findBoardsWithMetricsByUid(uid: Long, limit: Long, offset: Long): List<BoardDataWithMetrics>
    fun countBoardsByUid(uid: Long): Long
}

class BoardCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
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

    override fun findBoardsWithMetricsByUid(uid: Long, limit: Long, offset: Long): List<BoardDataWithMetrics> {
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
            .where(qBoard.uid.eq(uid))
            .orderBy(qBoard.id.desc())
            .offset(offset)
            .limit(limit)
            .fetch()
    }

    override fun countBoardsByUid(uid: Long): Long {
        return jpaQueryFactory
            .select(qBoard.count())
            .from(qBoard)
            .where(qBoard.uid.eq(uid))
            .fetchOne() ?: 0
    }
}