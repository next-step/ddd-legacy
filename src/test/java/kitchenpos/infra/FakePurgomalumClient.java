package kitchenpos.infra;

import java.util.Arrays;
import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {
    private final List<String> profanities = Arrays.asList("욕설", "비속어");

    @Override
    public boolean containsProfanity(final String text) {
        return profanities.stream()
                .anyMatch(text::contains);
    }
}
