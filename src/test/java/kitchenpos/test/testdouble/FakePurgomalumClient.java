package kitchenpos.test.testdouble;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {

    private final PurgomalumRule purgomalumRule;

    public FakePurgomalumClient(PurgomalumRule purgomalumRule) {
        super(new RestTemplateBuilder());
        this.purgomalumRule = purgomalumRule;
    }

    @Override
    public boolean containsProfanity(String text) {
        System.out.format("input: %s", text);
        return purgomalumRule.isContains();
    }
}
