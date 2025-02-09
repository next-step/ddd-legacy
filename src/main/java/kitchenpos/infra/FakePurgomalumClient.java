package kitchenpos.infra;

import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

public class FakePurgomalumClient extends PurgomalumClient {

    private static final List<String> profanities = List.of("fuck", "shit");

    public FakePurgomalumClient() {
        super(new RestTemplateBuilder());
    }

    @Override
    public boolean containsProfanity(final String text) {
        return profanities.contains(text);
    }
}
