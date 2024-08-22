package link.yologram.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    init()
    runApplication<Application>(*args)
}

fun init() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
}