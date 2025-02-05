package kitchenpos.infra;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestClientConfig {

    @Bean
    public PurgomalumClient purgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        return new FakePurgomalumClient(restTemplateBuilder);
    }
}
