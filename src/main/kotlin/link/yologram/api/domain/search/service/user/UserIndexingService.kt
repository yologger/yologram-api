package link.yologram.api.domain.search.service.user

import jakarta.annotation.PostConstruct
import link.yologram.api.config.OpensearchConfig
import link.yologram.api.domain.search.document.UserDocument
import link.yologram.api.domain.search.exception.UserNotFoundException
import link.yologram.api.domain.search.service.OpensearchService
import link.yologram.api.domain.ums.repository.UserRepository
import link.yologram.api.global.extension.readFileAsString
import link.yologram.api.global.extension.toJsonExcludeNull
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class UserIndexingService(
    private val osService: OpensearchService,
    private val userRepository: UserRepository,
    private val indexProperties: OpensearchConfig.UserIndexProperties
) {
    private val logger = LoggerFactory.getLogger(UserIndexingService::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    @PostConstruct
    fun init() {
        createTemplate()
        createIndexWithAlias()
    }

    fun index(uid: Long) {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("user not found: $uid") }
        val userDocument = UserDocument.Companion.of(user)
        val date = userDocument.joinedDate.format(formatter)
        val jsonDoc = user.toJsonExcludeNull()
        osService.indexDocument(
            index = indexProperties.indexName,
            json = jsonDoc,
            docId = "${user.id}"
        )
        logger.info("### Indexed User: [${user.id}]")
    }

    private fun createTemplate() {
        logger.info("### Upserting template: [${indexProperties.templateName}]")
        val settingsJson = indexProperties.templateSettingsFile.readFileAsString()
        val mappingsJson = indexProperties.templateMappingsFile.readFileAsString()

        osService.putTemplate(
            templateName = indexProperties.templateName,
            settingsJson = settingsJson,
            mappingsJson = mappingsJson,
            patterns = listOf("${indexProperties.indexPrefix}-*"),
            shards = indexProperties.shards,
            replicas = indexProperties.replicas,
        )

        logger.info("### Upserted Template: [${indexProperties.templateName}]")
    }

    fun createIndexWithAlias() {
        with(indexProperties) {
            logger.info("### Upserting Index: [${indexProperties.indexName}]")
            osService.createIndex(indexName)    // 예: "user-index-v1" 생성
            logger.info("### Upserted Index: [${indexProperties.indexName}]")
            logger.info("### Upserting Alias: [${indexProperties.indexAlias}]")
            osService.updateAliases(
                IndicesAliasesRequest().apply {
                    addAliasAction(
                        IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(indexName)                   // "user-index-v1"
                            .alias(indexAlias)                  // "user-index"
                            .writeIndex(true)       // "user-index-v1"를 "user-index"의 writeIndex로 지정
                    )

                    preVersions.forEach { previousVersion ->    // 예: ["v0"]
                        "$indexPrefix-$previousVersion".takeIf { osService.existsIndex(it) }?.let { // "user-index-v0"이 존재하면
                            addAliasAction(
                                IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                                    .index(it)                  // "user-index-v0"
                                    .alias(indexAlias)                  // "user-index"
                                    .writeIndex(false)      // "user-index-v0"은 readOnlyIndex로 설정
                            )
                        }
                    }
                }
            )
            logger.info("### Upserted Alias: [${indexProperties.indexAlias}]")
        }
    }
}