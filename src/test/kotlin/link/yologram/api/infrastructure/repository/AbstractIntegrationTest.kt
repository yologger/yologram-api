package link.yologram.api.infrastructure.repository

import link.yologram.api.confg.TestDataSourceConfig
import link.yologram.api.confg.TestMySQLContainer
import link.yologram.api.config.database.PersistentConfig
import link.yologram.api.config.database.QueryDslConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

//@ActiveProfiles("test", "mysql")
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(TestMySQLContainer::class, TestDataSourceConfig::class, PersistentConfig::class, QueryDslConfig::class)
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class AbstractIntegrationTest {
}