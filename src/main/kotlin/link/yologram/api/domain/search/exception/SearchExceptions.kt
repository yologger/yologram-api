package link.yologram.api.domain.search.exception

open class SearchException(message: String): Exception(message)

class UserNotFoundException(message: String): SearchException(message)

class BoardNotFoundException(message: String): SearchException(message)

