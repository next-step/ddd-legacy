package kitchenpos.support;

import kitchenpos.infra.PurgomalumClient;
import kitchenpos.stub.TestPurgomalumClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public PurgomalumClient purgomalumClient() {
        return new TestPurgomalumClient(new RestTemplateBuilder());
    }

}
