package kitchenpos.stub;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class TestPurgomalumClient extends PurgomalumClient {

    public TestPurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public boolean containsProfanity(String text) {
        return false;
    }
}
