package kitchenpos.infra;

public class FakePurgomalumClient implements PurgomalumClient {

    public static final String PROFANITY = "profanity";

    @Override
    public boolean containsProfanity(String text) {
        return PROFANITY.equalsIgnoreCase(text);
    }
}
