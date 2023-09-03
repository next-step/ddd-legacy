package kitchenpos.fake;

import kitchenpos.infra.ProfanityClient;

import java.util.Set;

public class FakeProfanityClient implements ProfanityClient {
    private final Set<String> profanities = Set.of("욕설", "비속어", "욕");

    @Override
    public boolean containsProfanity(String text) {
        if (profanities.contains(text)) {
            return true;
        }
        return false;
    }
}