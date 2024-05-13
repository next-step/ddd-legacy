package kitchenpos.utils;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

@Component
public class PurgomalumTestClient extends PurgomalumClient {

    public PurgomalumTestClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public boolean containsProfanity(String text) {
        return false;
    }
}
