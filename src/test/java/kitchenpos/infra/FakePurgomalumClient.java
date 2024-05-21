package kitchenpos.infra;

import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {
    private static final List<String> 욕설목록 = List.of("욕설");

    @Override
    public boolean containsProfanity(String text) {
        return 욕설목록.stream().anyMatch(욕설단어 -> text.contains(욕설단어));
    }
}
