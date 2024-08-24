package link.yologram.api.infrastructure.repository

import link.yologram.api.confg.TestDataSourceConfig
import link.yologram.api.confg.TestMySQLContainer
import link.yologram.api.config.database.PersistentConfig
import link.yologram.api.config.database.QueryDslConfig
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest(showSql = true)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Disable H2 on DataJpaTest
@Import(TestMySQLContainer::class, TestDataSourceConfig::class, PersistentConfig::class, QueryDslConfig::class)
abstract class AbstractDataJpaTest {

}