package link.yologram.api.global.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/** Wrap Response Ok */
fun <T> T.wrapOk() = ResponseEntity.ok(this)

/** Wrap Response Conflict */
fun <T> T.wrapConflict() = ResponseEntity.status(HttpStatus.CONFLICT).body(this)

/** Wrap Response Created */
fun <T> T.wrapCreated() = ResponseEntity.status(HttpStatus.CREATED).body(this)

/** Wrap Bad Request */
fun <T> T.wrapBadRequest() = ResponseEntity.badRequest().body(this)

/** Wrap Unauthorized */
fun <T> T.wrapUnauthorized() = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(this)

/** Internal Server Error */
fun <T> T.wrapInternalServerError() = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this)

/** Method Not Allowed */
fun <T> T.wrapMethodNotAllowed() = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(this)

/** Not Found */
fun <T> T.wrapNotFound() = ResponseEntity.status(HttpStatus.NOT_FOUND).body(this)

/** Wrap Response Void */
fun Unit.wrapVoid() = ResponseEntity.noContent()