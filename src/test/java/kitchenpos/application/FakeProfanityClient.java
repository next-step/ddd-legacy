package kitchenpos.application;

import java.util.HashSet;
import java.util.Set;
import kitchenpos.infra.ProfanityClient;

public class FakeProfanityClient implements ProfanityClient {

    private static final Set<String> profanityDictionary = new HashSet<>();

    static {
        profanityDictionary.add("욕");
        profanityDictionary.add("비속어");
        profanityDictionary.add("나쁜말");
    }

    @Override
    public boolean containsProfanity(final String text) {
        return profanityDictionary.stream()
            .anyMatch(text::contains);
    }
}
