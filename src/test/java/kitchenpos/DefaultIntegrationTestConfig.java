package kitchenpos;

import static kitchenpos.constant.Profile.TEST;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = TEST)
public class DefaultIntegrationTestConfig {

}
