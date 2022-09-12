package kitchenpos.application;

import java.util.Set;

public class TestProfanityChecker implements ProfanityChecker {
    private final Set<String> profanities = Set.of("나쁜말", "심한말");
    @Override
    public boolean containsProfanity(String text) {
        return profanities.contains(text);
    }
}
