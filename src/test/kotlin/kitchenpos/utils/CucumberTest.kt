package kitchenpos.utils

import io.cucumber.spring.CucumberContextConfiguration
import kitchenpos.Application
import org.springframework.boot.test.context.SpringBootTest

@CucumberContextConfiguration
@SpringBootTest(classes = [Application::class])
class CucumberTest
