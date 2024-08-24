import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
inline fun <reified T> spy(): T = Mockito.spy(T::class.java)

inline fun <reified T> any(): T {
    ArgumentMatchers.any(T::class.java)
    return createInstance()
}

inline fun <reified T> createInstance(): T = when (T::class) {
    Int::class -> 0 as T
    Long::class -> 0L as T
    Set::class -> emptySet<Any>() as T
    List::class -> emptyList<Any>() as T
    else -> castNull()
}

fun <T> castNull(): T {
    @Suppress("UNCHECKED_CAST")
    return null as T
}