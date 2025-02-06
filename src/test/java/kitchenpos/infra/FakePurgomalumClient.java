package kitchenpos.infra;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FakePurgomalumClient implements PurgomalumClient {

    private final Set<String> profanityWords;

    public FakePurgomalumClient() {
        this.profanityWords = new HashSet<>(List.of("욕설1", "욕설2", "비속어1", "비속어2"));
    }

    public FakePurgomalumClient(Set<String> profanityWords) {
        this.profanityWords = new HashSet<>(profanityWords);
    }

    @Override
    public boolean containsProfanity(String text) {
        return profanityWords.stream()
                .anyMatch(text::contains);
    }
}