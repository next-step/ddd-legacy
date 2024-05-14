package kitchenpos.acceptacne;

import kitchenpos.infra.PurgomalumClient;
import kitchenpos.infra.FakePurgomalumClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AcceptanceTestConfig {
    @Bean
    public PurgomalumClient purgomalumClient() {
        return new FakePurgomalumClient();
    }
}
