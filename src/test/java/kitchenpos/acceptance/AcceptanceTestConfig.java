package kitchenpos.acceptance;

import kitchenpos.fixture.fake.FakeProfanityClient;
import kitchenpos.domain.ProfanityClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AcceptanceTestConfig {

    @Bean
    @Primary
    public ProfanityClient fakeProfanityClient() {
        return new FakeProfanityClient();
    }
}
