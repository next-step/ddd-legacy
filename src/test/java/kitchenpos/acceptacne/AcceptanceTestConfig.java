package kitchenpos.acceptacne;

import kitchenpos.infra.PurgomalumClient;
import kitchenpos.infra.FakePurgomalumClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AcceptanceTestConfig {
    @Bean
    @Primary
    public PurgomalumClient purgomalumClient() {
        return new FakePurgomalumClient();
    }
}
