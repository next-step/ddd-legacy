package kitchenpos.utils

import io.cucumber.spring.CucumberContextConfiguration
import kitchenpos.Application
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT

@CucumberContextConfiguration
@SpringBootTest(classes = [Application::class], webEnvironment = DEFINED_PORT)
class CucumberTest
