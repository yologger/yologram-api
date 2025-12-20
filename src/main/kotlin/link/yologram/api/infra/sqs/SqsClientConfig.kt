package link.yologram.api.infra.sqs

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient as AwsSqsClient

@Configuration
class SqsClientConfig(
) {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun sqsClient(
        objectMapper: ObjectMapper,
        @Value("\${spring.cloud.aws.credentials.instance-profile:false}") useInstanceProfile: Boolean,
        @Value("\${spring.cloud.aws.credentials.profile.name:default}") profileName: String
    ): SqsClient {
        logger.info { "Initialized SQS Client" }
        val awsClient = AwsSqsClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(
                if (useInstanceProfile) {
                    logger.info { "Using EC2 Instance Profile for SQS credentials" }
                    InstanceProfileCredentialsProvider.create()
                } else {
                    logger.info { "Using AWS Profile '$profileName' for SQS credentials" }
                    ProfileCredentialsProvider.create(profileName)
                }
            )

            .build()
        return SqsClientImpl(awsClient, objectMapper)
    }
}