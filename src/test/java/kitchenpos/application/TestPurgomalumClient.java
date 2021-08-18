package kitchenpos.application;

import kitchenpos.domain.PurgomalumClient;

import java.util.Arrays;
import java.util.List;

public class TestPurgomalumClient implements PurgomalumClient {
    private final List<String> profanities = Arrays.asList("레거시", "하드코딩");

    @Override
    public boolean containsProfanity(String text) {
        return profanities.contains(text);
    }
}
