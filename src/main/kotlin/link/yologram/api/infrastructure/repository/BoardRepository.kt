package link.yologram.api.infrastructure.repository

import link.yologram.api.infrastructure.entity.Board
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository: JpaRepository<Board, Long>, BoardCustomRepository {
}