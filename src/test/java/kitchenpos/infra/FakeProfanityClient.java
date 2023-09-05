package kitchenpos.infra;

import java.util.List;

public class FakeProfanityClient implements ProfanityClient {
    private final List<String> profanityWords = List.of("욕설", "비난", "심한말");

    public FakeProfanityClient() {
    }

    @Override
    public boolean containsProfanity(final String text) {
        return profanityWords.stream()
            .anyMatch(text::contains);
    }
}
