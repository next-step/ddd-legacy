package kitchenpos.infra;

import java.util.Arrays;

public class FakeProfanityDetectClient implements ProfanityDetectClient {

    public static final String[] PROFANITY_WORDS = {
            "holiday", "bed", "anxious", "everyday", "reach",
    };

    @Override
    public boolean containsProfanity(String text) {
        return Arrays.stream(PROFANITY_WORDS)
                .anyMatch(text::contains);
    }
}
