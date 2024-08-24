package link.yologram.api.confg

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container

@TestConfiguration("TestMySQLContainer")
class TestMySQLContainer {
    companion object {

        @Container
        @JvmStatic
        val container = MySQLContainer<Nothing>("mysql:8.0.23")
            .apply {
                withDatabaseName("testdb")
                withUsername("root")
                withPassword("rootroot")
                withInitScript("sql/schema.sql")
            }
            .apply {
                start()
            }
    }
}