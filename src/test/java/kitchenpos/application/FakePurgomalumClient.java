package kitchenpos.application;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {

    public FakePurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(new RestTemplateBuilder());
    }

    @Override
    public boolean containsProfanity(String text) {
        return "비속어".equals(text);
    }
}
