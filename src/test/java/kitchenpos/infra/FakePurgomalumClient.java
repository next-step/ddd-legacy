package kitchenpos.infra;

import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {
    List<String> profanityList = List.of("욕설", "비속어", "나쁜말");

    @Override
    public boolean containsProfanity(String text) {
        return profanityList.stream()
                .anyMatch(text::contains);
    }
}
