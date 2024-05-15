package kitchenpos.fakeClient;


import kitchenpos.infra.PurgomalumClient;

public class FakePurgomalumClient implements PurgomalumClient {
    private static final String PROFANITY_TEXT = "바보";

    public FakePurgomalumClient() {
    }

    public boolean containsProfanity(final String text) {
        return (text.contains(PROFANITY_TEXT) ? true : false);
    }
}
