package kitchenpos.fixture.fake;

import kitchenpos.domain.ProfanityClient;

public class FakeProfanityClient implements ProfanityClient {

    @Override
    public boolean containsProfanity(String text) {
        return text.contains("욕설") || text.contains("비속어");
    }
}
