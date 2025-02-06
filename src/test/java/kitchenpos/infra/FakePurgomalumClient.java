package kitchenpos.infra;

import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {

    private final List<String> profanities;

    public FakePurgomalumClient(List<String> profanities) {
        this.profanities = profanities;
    }

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream()
                .anyMatch(text::contains);
    }
}
