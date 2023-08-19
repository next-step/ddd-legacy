package kitchenpos.utils.fake;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {

    private FakePurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    public FakePurgomalumClient(){
        super(new RestTemplateBuilder());
    }

    @Override
    public boolean containsProfanity(String text) {
        FakePurgomalumResponse response = FakePurgomalumResponse.valueOf(text);
        return response.containsProfanity();
    }
}
