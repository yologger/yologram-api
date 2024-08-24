package link.yologram.api.infrastructure.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import link.yologram.api.infrastructure.entity.Board
import link.yologram.api.infrastructure.entity.QBoard
import org.springframework.transaction.support.TransactionTemplate

class BoardCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val template: TransactionTemplate
): BoardCustomRepository {

    private val qBoard = QBoard.board

    override fun findBoardsByUidOrderByCreateDateDesc(uid: Long, page: Long, size: Long): List<Board> {
        return template.execute {
            jpaQueryFactory.selectFrom(qBoard)
                .where(qBoard.uid.eq(uid))
                .offset(page * size)
                .limit(size)
                .orderBy(qBoard.id.desc())
                .fetch()
        } ?: emptyList()
    }
}