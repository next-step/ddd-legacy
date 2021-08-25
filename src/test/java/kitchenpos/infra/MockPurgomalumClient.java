package kitchenpos.infra;

public class MockPurgomalumClient implements PurgomalumClient {
    @Override
    public boolean containsProfanity(String text) {
        // default
        return false;
    }
}
