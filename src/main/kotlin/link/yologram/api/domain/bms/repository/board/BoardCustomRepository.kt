package link.yologram.api.domain.bms.repository.board

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.bms.entity.QBoard
import link.yologram.api.domain.bms.entity.QBoardCommentCount
import link.yologram.api.domain.bms.entity.QBoardLikeCount
import link.yologram.api.domain.bms.entity.QBoardViewCount
import link.yologram.api.domain.ums.entity.QUser

interface BoardCustomRepository {
    fun findBoardWithMetricsById(id: Long): BoardDataWithMetrics?
    fun findBoards(cursorId: Long?, pageSize: Long): List<BoardData>
    fun findBoardsWithMetrics(cursorId: Long?, pageSize: Long): List<BoardDataWithMetrics>
    fun findBoardsWithMetricsByUid(uid: Long, limit: Long, offset: Long): List<BoardDataWithMetrics>
    fun countBoardsByUid(uid: Long): Long
}

class BoardCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): BoardCustomRepository {

    private val qBoard = QBoard.board
    private val qUser = QUser.user
    private val qBoardCommentCount = QBoardCommentCount.boardCommentCount
    private val qBoardLikeCount = QBoardLikeCount.boardLikeCount
    private val qBoardViewCount = QBoardViewCount.boardViewCount

    override fun findBoardWithMetricsById(id: Long): BoardDataWithMetrics? {

        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardDataWithMetrics::class.java,
                    qBoard.id,
                    qBoard.title,
                    qBoard.content,
                    qBoard.createdDate,
                    qBoard.modifiedDate,
                    Projections.constructor(
                        BoardDataWithMetrics.Writer::class.java,
                        qUser.id,
                        qUser.name,
                        qUser.nickname,
                        qUser.avatar
                    ),
                    Projections.constructor(
                        BoardDataWithMetrics.Metrics::class.java,
                        qBoardCommentCount.count.coalesce(0L),
                        qBoardLikeCount.count.coalesce(0L),
                        qBoardViewCount.count.coalesce(0L),
                    )
                )
            )
            .from(qBoard)
            .leftJoin(qBoardCommentCount)
            .on(qBoard.id.eq(qBoardCommentCount.bid))
            .leftJoin(qBoardLikeCount)
            .on(qBoard.id.eq(qBoardLikeCount.bid))
            .leftJoin(qBoardViewCount)
            .on(qBoard.id.eq(qBoardViewCount.bid))
            .leftJoin(qUser)
            .on(qBoard.uid.eq(qUser.id))
            .where(qBoard.id.eq(id))
            .fetchOne()
    }

    /** cursor-based pagination (Infinite Scrolling) **/
    override fun findBoards(cursorId: Long?, pageSize: Long): List<BoardData> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardData::class.java,
                    qBoard.id,
                    qBoard.uid,
                    qBoard.title,
                    qBoard.content,
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

    /** cursor-based pagination (Infinite Scrolling) **/
    override fun findBoardsWithMetrics(cursorId: Long?, pageSize: Long): List<BoardDataWithMetrics> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardDataWithMetrics::class.java,
                    qBoard.id,
                    qBoard.title,
                    qBoard.content,
                    qBoard.createdDate,
                    qBoard.modifiedDate,
                    Projections.constructor(
                        BoardDataWithMetrics.Writer::class.java,
                        qUser.id,
                        qUser.name,
                        qUser.nickname,
                        qUser.avatar
                    ),
                    Projections.constructor(
                        BoardDataWithMetrics.Metrics::class.java,
                        qBoardCommentCount.count.coalesce(0L),
                        qBoardLikeCount.count.coalesce(0L),
                        qBoardViewCount.count.coalesce(0L),
                    )
                )
            )
            .from(qBoard)
            .leftJoin(qBoardCommentCount)
            .on(qBoard.id.eq(qBoardCommentCount.bid))
            .leftJoin(qBoardLikeCount)
            .on(qBoard.id.eq(qBoardLikeCount.bid))
            .leftJoin(qBoardViewCount)
            .on(qBoard.id.eq(qBoardViewCount.bid))
            .leftJoin(qUser)
            .on(qBoard.uid.eq(qUser.id))
            .where(cursorId?.let { qBoard.id.lt(it) })
            .orderBy(qBoard.id.desc())
            .limit(pageSize)
            .fetch()
    }

    /** offset-based pagination (Pagination Bar) **/
    override fun findBoardsWithMetricsByUid(uid: Long, limit: Long, offset: Long): List<BoardDataWithMetrics> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    BoardDataWithMetrics::class.java,
                    qBoard.id,
                    qBoard.title,
                    qBoard.content,
                    qBoard.createdDate,
                    qBoard.modifiedDate,
                    Projections.constructor(
                        BoardDataWithMetrics.Writer::class.java,
                        qUser.id,
                        qUser.name,
                        qUser.nickname,
                        qUser.avatar
                    ),
                    Projections.constructor(
                        BoardDataWithMetrics.Metrics::class.java,
                        qBoardCommentCount.count.coalesce(0L),
                        qBoardLikeCount.count.coalesce(0L),
                        qBoardViewCount.count.coalesce(0L),
                    )
                )
            )
            .from(qBoard)
            .leftJoin(qBoardCommentCount)
            .on(qBoard.id.eq(qBoardCommentCount.bid))
            .leftJoin(qBoardLikeCount)
            .on(qBoard.id.eq(qBoardLikeCount.bid))
            .leftJoin(qBoardViewCount)
            .on(qBoard.id.eq(qBoardViewCount.bid))
            .leftJoin(qUser)
            .on(qBoard.uid.eq(qUser.id))
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