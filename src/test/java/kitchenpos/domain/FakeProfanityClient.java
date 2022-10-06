package kitchenpos.domain;

import kitchenpos.infra.ProfanityClient;

import java.util.List;

public class FakeProfanityClient implements ProfanityClient {
    private static final List<String> profanities = List.of("욕설", "비속어");
    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
                .anyMatch(profanity -> text.contains(profanity));
    }
}
