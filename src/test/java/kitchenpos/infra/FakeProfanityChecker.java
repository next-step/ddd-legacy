package kitchenpos.infra;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FakeProfanityChecker implements ProfanityChecker {

    private final Set<String> profanityWords;

    public FakeProfanityChecker() {
        this.profanityWords = new HashSet<>(List.of("욕설1", "욕설2", "비속어1", "비속어2"));
    }

    public FakeProfanityChecker(Set<String> profanityWords) {
        this.profanityWords = new HashSet<>(profanityWords);
    }

    @Override
    public boolean contains(String text) {
        return profanityWords.stream()
                .anyMatch(text::contains);
    }
}