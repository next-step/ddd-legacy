package kitchenpos.fake;

import kitchenpos.infra.PurgomalumClient;

public class FakePurgomalumClient implements PurgomalumClient {

    @Override
    public boolean containsProfanity(String text) {
        return "bitch".equals(text);
    }
}
