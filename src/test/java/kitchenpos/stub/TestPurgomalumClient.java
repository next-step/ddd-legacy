package kitchenpos.stub;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.web.client.RestTemplateBuilder;

@TestComponent
public class TestPurgomalumClient extends PurgomalumClient {

    public TestPurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public boolean containsProfanity(String text) {
        return false;
    }
}
