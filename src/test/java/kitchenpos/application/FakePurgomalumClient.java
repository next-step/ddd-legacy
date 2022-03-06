package kitchenpos.application;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {
    private boolean containsNotAllowedWords;

    public FakePurgomalumClient(boolean containsNotAllowedWords) {
        super(new RestTemplateBuilder());
        this.containsNotAllowedWords = containsNotAllowedWords;
    }

    @Override
    public boolean containsProfanity(String text) {
        return containsNotAllowedWords;
    }
}
