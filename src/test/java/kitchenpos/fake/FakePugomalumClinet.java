package kitchenpos.fake;

import java.util.Set;
import kitchenpos.infra.DefaultPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePugomalumClinet implements PurgomalumClient {
    private final Set<String> pugmalums = Set.of("비속어", "욕설");

    public boolean containsProfanity(String text) {
        return pugmalums.contains(text);
    }
}
