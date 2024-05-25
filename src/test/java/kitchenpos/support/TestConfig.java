package kitchenpos.support;

import kitchenpos.infra.KitchenridersClient;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.stub.TestKitchenridersClient;
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

    @Bean
    public KitchenridersClient kitchenridersClient() {
        return new TestKitchenridersClient();
    }

}
