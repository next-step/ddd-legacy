package kitchenpos.fake;

import java.util.Set;
import kitchenpos.infra.PurgomalumClient;

public class FakePugomalumClinet implements PurgomalumClient {
    private final Set<String> pugmalums = Set.of("비속어", "욕설");

    public boolean containsProfanity(String text) {
        return pugmalums.contains(text);
    }
}
