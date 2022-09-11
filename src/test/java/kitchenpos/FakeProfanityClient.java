package kitchenpos;

import kitchenpos.infra.ProfanityClient;

public class FakeProfanityClient implements ProfanityClient {
    @Override
    public boolean containsProfanity(String text) {
        return false;
    }
}
