package kitchenpos.config;

import java.util.List;
import kitchenpos.infra.PurgomalumClient;

public class FakePurgomalumClient implements PurgomalumClient {

    private static final List<String> profanities = List.of("비속어", "욕설");

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
                .anyMatch(text::contains);
    }
}
