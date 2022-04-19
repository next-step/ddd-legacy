package kitchenpos.infra;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FakeProfanityClient implements ProfanityClient {
    private List<String> profanities = Arrays.asList("욕설", "비속어", "욕");

    @Override
    public boolean containsProfanity(final String text) {
        return profanities.stream()
                .anyMatch(profanity -> Objects.equals(profanity, text));
    }
}
