package kitchenpos.fake.repository;

import kitchenpos.infra.PurgomalumClient;

public class FakePurogmalumClient implements PurgomalumClient {
    @Override
    public boolean containsProfanity(String text) {
        return false;
    }
}
