package link.yologram.api.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@EnableAsync
@Configuration
class AsyncConfig {

    @Bean
    fun searchMigrationExecutor(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 1  // 최소한 유지할 스레드 수
            queueCapacity = QUEUE_CAPACITY // 모든 thread가 사용 중이면 task는 queue에서 대기
            maxPoolSize = 10  // queue도 꽉 차면 늘어날 수 있는 최대 스레드 스레드가 모두 사용 중이라 작업대기열에서 대기 중일 때 늘어날 수 있는 최대 스레드 수
            keepAliveSeconds = 60  // idle 스레드 종료 전 대기 시간
            setWaitForTasksToCompleteOnShutdown(true) // 어플리케이션 종료 시 queue에서 대기 중인 task 완료까지 대기
            setAwaitTerminationSeconds(30) // 종료 전 최대 대기 시간
            setTaskDecorator(SearchMigrationTaskDecorator(this)) // 작업 처리 전/후 처리를 위한 decorator
            setThreadNamePrefix("search-migration-executor")
        }
    }
}

private const val QUEUE_CAPACITY = 20

private val logger = KotlinLogging.logger {}

private class SearchMigrationTaskDecorator(private val executor: ThreadPoolTaskExecutor) : TaskDecorator {

    override fun decorate(task: Runnable): Runnable {
        return Runnable {
            val currentQueueSize = executor.threadPoolExecutor.queue.size
            if (currentQueueSize >= (QUEUE_CAPACITY / 2)) {
                logger.warn { "poolSize : ${executor.poolSize}, queueSize : ${currentQueueSize},  activeCount : ${executor.activeCount}" }
            }
            task.run()
        }
    }
}