package kitchenpos.integration.mock;

import kitchenpos.domain.ProfanityChecker;

import java.util.List;

public class FakeProfanityChecker implements ProfanityChecker {

    private static final List<String> PROFANE_WORDS = List.of("바보");

    @Override
    public boolean containsProfanity(String text) {
        return PROFANE_WORDS.stream()
                .anyMatch(it -> it.contains(text));
    }
}
