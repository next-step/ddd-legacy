package kitchenpos.helper;

import java.util.List;
import kitchenpos.domain.ProfanityClient;

public class InMemoryProfanityClient implements ProfanityClient {

    private final List<String> profanities = List.of("욕설", "비속어");

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
            .anyMatch(text::contains);
    }
}
