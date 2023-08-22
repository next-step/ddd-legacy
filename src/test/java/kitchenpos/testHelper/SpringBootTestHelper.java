package kitchenpos.testHelper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest(properties = {
    "spring.config.location =" + "classpath:/application-test.properties"})
@Component
public abstract class SpringBootTestHelper {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    public void init() {
        databaseCleanup.execute();
    }

}
