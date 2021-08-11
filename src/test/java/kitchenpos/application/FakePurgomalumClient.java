package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import kitchenpos.infra.PurgomalumClient;

public class FakePurgomalumClient implements PurgomalumClient {

    private static final List<String> profanities;

    static {
        profanities = Arrays.asList("fuck", "damn", "bitch");
    }

    @Override
    public boolean containsProfanity(final String text) {
        return profanities.stream()
            .anyMatch(text::contains);
    }
}
