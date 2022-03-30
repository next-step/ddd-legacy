package kitchenpos.testdouble;

import kitchenpos.infra.ProfanityClient;

import java.util.Arrays;
import java.util.List;

public class ProfanityClientStub implements ProfanityClient {

    private final List<String> profanities = Arrays.asList("xxx", "욕설");

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
                .anyMatch(profanity -> profanity.contains(text));
    }
}
