package kitchenpos.application;

import java.util.Set;
import kitchenpos.infra.PurgomalumClient;

public class FakePurgomalumClient implements PurgomalumClient {

    private static final Set<String> profanities = Set.of("대충 나쁜 말");

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
            .anyMatch(text::contains);
    }
}
