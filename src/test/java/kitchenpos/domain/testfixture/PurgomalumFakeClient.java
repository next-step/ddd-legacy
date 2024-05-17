package kitchenpos.domain.testfixture;

import kitchenpos.infra.PurgomalumClient;

public class PurgomalumFakeClient implements PurgomalumClient {

    public boolean containsProfanity(String text) {
        return text.contains("비속어");
    }
}
