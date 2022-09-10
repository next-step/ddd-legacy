package kitchenpos.fake;

import java.util.Set;
import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePugomalumClinet extends PurgomalumClient {
    private final Set<String> pugmalumSet = Set.of("비속어", "욕설");

    public FakePugomalumClinet(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    public boolean containsProfanity(String text) {
        return pugmalumSet.contains(text);
    }
}
