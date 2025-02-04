package kitchenpos.infra;


import java.util.Arrays;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {

    public FakePurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    private static final List<String> profanities;

    static {
        profanities = Arrays.asList("비속어", "욕설");
    }


    @Override
    public boolean containsProfanity(final String text) {
        return profanities.stream()
                          .anyMatch(text::contains);
    }
}
