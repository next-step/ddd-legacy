package kitchenpos.infra;

import java.util.List;

public class FakeProfanityClient implements PurgomalumClient {

    private final List<String> profanities;

    public FakeProfanityClient() {
        this(List.of());
    }

    public FakeProfanityClient(List<String> profanities) {
        this.profanities = profanities;
    }

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
            .anyMatch(profanity -> profanity.contains(text));
    }
}