package com.algocup.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.algocup.ApplicationConfiguration
import com.algocup.codegeneration.JavaCodeGenerator
import com.algocup.compile.InMemoryJavaCompiler
import com.algocup.data.JsonProblemsRepository
import com.algocup.data.ProblemsRepository
import com.algocup.judge.JvmJudgeEngine
import com.algocup.web.ProblemsController
import com.algocup.web.SubmissionsListener
import com.nhaarman.mockito_kotlin.whenever
import org.apache.kafka.clients.producer.Producer
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
open class TestApplicationConfiguration : ApplicationConfiguration() {

    @Bean
    open fun problemsRepository(): ProblemsRepository =
            JsonProblemsRepository()

    @Bean
    open fun judgeEngine(objectMapper: ObjectMapper) = JvmJudgeEngine(objectMapper, InMemoryJavaCompiler())

    @Bean
    open fun submissionsListener(
            problemsRepository: ProblemsRepository,
            judgeEngine: JvmJudgeEngine,
            objectMapper: ObjectMapper
    ) = SubmissionsListener(problemsRepository, judgeEngine, objectMapper)

    @Bean
    open fun problemsController(problemsRepository: ProblemsRepository) =
            ProblemsController(problemsRepository, JavaCodeGenerator())

    @Bean
    open fun kafkaTemplate(): KafkaTemplate<*, *> {
        val producerFactory = mock(ProducerFactory::class.java)
        val producer = mock(Producer::class.java)
        whenever(producerFactory.createProducer()).thenReturn(producer)
        return KafkaTemplate(producerFactory)
    }

}

