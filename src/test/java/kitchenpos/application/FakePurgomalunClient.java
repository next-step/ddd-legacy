package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import kitchenpos.infra.PurgomalumClient;

public class FakePurgomalunClient implements PurgomalumClient {

    private static final List<String> profanities;

    static {
        profanities = Arrays.asList("비속어", "욕설");
    }

    @Override
    public boolean containsProfanity(final String text) {
        return profanities.stream()
            .anyMatch(text::contains);
    }
}
