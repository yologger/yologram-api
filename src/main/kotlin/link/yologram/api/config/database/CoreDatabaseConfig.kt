package link.yologram.api.config.database

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["link.yologram.api"],
    entityManagerFactoryRef = "coreEntityManager",
    transactionManagerRef = "coreTransactionManager"
)
class CoreDatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "core.master.datasource")
    fun coreMasterDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "core.master.datasource.hikari")
    fun coreMasterHikariDataSource(@Qualifier("coreMasterDataSourceProperties") masterProperty: DataSourceProperties): HikariDataSource {
        return masterProperty.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    @Bean
    @ConfigurationProperties(prefix = "core.slave.datasource")
    fun coreSlaveDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @ConfigurationProperties(prefix = "core.slave.datasource.hikari")
    fun coreSlaveHikariDataSource(@Qualifier("coreSlaveDataSourceProperties") slaveProperty: DataSourceProperties): HikariDataSource {
        return slaveProperty.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    @Bean
    fun coreRoutingDataSource(
        @Qualifier("coreMasterHikariDataSource") masterDataSource: DataSource,
        @Qualifier("coreSlaveHikariDataSource") slaveDataSource: DataSource
    ): DataSource {
        val dataSourceMap: Map<Any, Any> = hashMapOf(
            DBType.MASTER to masterDataSource,
            DBType.SLAVE to slaveDataSource
        )

        return LazyConnectionDataSourceProxy(MasterSlaveRoutingDataSource().apply {
            this.setDefaultTargetDataSource(masterDataSource)
            this.setTargetDataSources(dataSourceMap)
            this.afterPropertiesSet()
        })
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "core.jpa")
    fun coreJpaProperties(): JpaProperties {
        return JpaProperties()
    }

    @Bean
    @Primary
    fun coreEntityManager(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("coreRoutingDataSource") coreRoutingDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(coreRoutingDataSource)
            .packages("link.yologram.api")
            .build()
    }

    @Bean
    @Primary
    fun coreTransactionManager(coreEntityManager: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(coreEntityManager)
    }
}