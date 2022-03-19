package kitchenpos.infra;

import org.springframework.boot.web.client.RestTemplateBuilder;

public class MockPurgomalumClient extends PurgomalumClient {

    public MockPurgomalumClient() {
        super(new RestTemplateBuilder());
    }

    @Override
    public boolean containsProfanity(String text) {
        return text.contains("f**k");
    }
}
