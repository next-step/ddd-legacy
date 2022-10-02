package kitchenpos.application.fake;

import kitchenpos.domain.ProfanityClient;

public class FakeProfanityClient implements ProfanityClient {
    @Override
    public boolean containsProfanity(String text) {
        return "비속어".equals(text);
    }
}
