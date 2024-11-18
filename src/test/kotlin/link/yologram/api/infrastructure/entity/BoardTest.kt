package link.yologram.api.infrastructure.entity

import jakarta.persistence.EntityManager
import link.yologram.api.infrastructure.repository.AbstractDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardTest(
    @Autowired private val entityManager: EntityManager
): AbstractDataJpaTest()  {

    @Test
    fun `엔티티 테스트`() {
        val user = User(id=1, email="tester@gmail.com", name="tester", nickname = "tester", password = "asAS!@12")
        entityManager.persist(user)

        val board1 = Board(uid=1, title = "board1 title", body = "board1 body")
        val board2 = Board(uid=2, title = "board2 title", body = "board2 body")
        entityManager.persist(board1)
        entityManager.persist(board2)

        assertThat(1).isEqualTo(1)
    }
}