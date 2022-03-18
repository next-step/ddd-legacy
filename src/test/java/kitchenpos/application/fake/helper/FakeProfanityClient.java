package kitchenpos.application.fake.helper;

import kitchenpos.infra.ProfanityClient;

import java.util.Collections;
import java.util.List;

public class FakeProfanityClient implements ProfanityClient {

    private static final List<String> PROFANITIES = Collections.singletonList("존X");

    @Override
    public boolean containsProfanity(String text) {
        return PROFANITIES.stream()
                .anyMatch(text::contains);
    }


}
