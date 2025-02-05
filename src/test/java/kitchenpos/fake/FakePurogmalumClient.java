package kitchenpos.fake;

import kitchenpos.infra.PurgomalumClient;

import java.util.Set;

public class FakePurogmalumClient implements PurgomalumClient {
    private static final Set<String> PROFANITY_WORDS = Set.of(
            "바보",
            "멍청이"
    );

    @Override
    public boolean containsProfanity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        return PROFANITY_WORDS.stream()
                .anyMatch(text.toLowerCase()::contains);
    }
}
