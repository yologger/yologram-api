package link.yologram.api.domain.bms.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import org.springframework.beans.factory.annotation.Autowired

class BoardViewCountRepositoryTest(
    @Autowired private val boardViewCountRepository: BoardViewCountRepository
) : AbstractRepositoryDataJpaTest() {

}