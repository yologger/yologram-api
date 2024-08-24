package link.yologram.api.rest

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.ResponseSpec.isOk(contentType: String = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE) = this
    .expectStatus().isOk
    .expectHeader().contentType(contentType)

fun WebTestClient.ResponseSpec.isCreated(contentType: String = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE) = this
    .expectStatus().isCreated
    .expectHeader().contentType(contentType)

fun WebTestClient.ResponseSpec.isNoContent() = this.expectStatus().isNoContent