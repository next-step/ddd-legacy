package kitchenpos.infra;

import java.util.Arrays;
import java.util.List;

public
class FakeProfanityClient implements ProfanityClient {

    private List<String> profanities = Arrays.asList("욕설", "비속어");

    @Override
    public boolean containsProfanity(String text) {
        return profanities.stream().anyMatch(text::contains);
    }
}
