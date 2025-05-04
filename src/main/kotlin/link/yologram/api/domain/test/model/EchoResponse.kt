package link.yologram.api.domain.test.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_EMPTY)
class EchoResponse {

    companion object {
        const val HOST = "host"
        const val PROTOCOL = "protocol"
        const val METHOD = "method"
        const val HEADERS = "headers"
        const val COOKIES = "cookies"
        const val PARAMETERS = "parameters"
        const val PATH = "path"
        const val BODY = "body"
        const val QUERYSTRING = "querystring"
    }

    private val attributes: MutableMap<String, Any> = HashMap()

    @JsonAnySetter
    fun set(name: String, value: Any) {
        attributes[name] = value
    }

    @JsonAnyGetter
    fun getAttributes(): Map<String, Any> {
        return attributes
    }
}