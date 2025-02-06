package kitchenpos.utils

import io.cucumber.java.Before
import org.springframework.beans.factory.annotation.Autowired


class CucumberHook : CucumberTest() {

    @Autowired
    private lateinit var databaseCleanup: DatabaseCleanup

    @Before
    fun cleanUp() {
        databaseCleanup.execute()
    }
}
