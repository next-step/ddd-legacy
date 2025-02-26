package kitchenpos.fake;

import kitchenpos.infra.PurgomalumClient;

import java.util.Arrays;
import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {

    private static final List<String> profanities;

    static {
        profanities = Arrays.asList("부적절한이름", "욕설");
    }

    @Override
    public boolean containsProfanity(final String text) {
        return profanities.stream()
                .anyMatch(text::contains);
    }
}
