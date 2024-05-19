package kitchenpos.application;

import kitchenpos.infra.PurgomalumClient;

import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {

    private static final List<String> profanity = List.of("비속어", "욕설", "욕");

    @Override
    public boolean containsProfanity(final String text) {

        return profanity.stream().anyMatch(text::contains);

    }
}

