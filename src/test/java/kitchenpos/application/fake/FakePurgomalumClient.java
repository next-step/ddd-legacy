package kitchenpos.application.fake;

import kitchenpos.infra.purgomalum.PurgomalumClient;

public class FakePurgomalumClient implements PurgomalumClient {

    private boolean profanity = false;

    public void changeProfanity(final boolean profanity) {
        this.profanity = profanity;
    }

    @Override
    public boolean containsProfanity(String text) {
        return this.profanity;
    }
}
