package kitchenpos.infra;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@Primary
@Component
public class PurgomalumFakeClient extends PurgomalumClient {
    List<String> profanityList = List.of("욕설", "비속어", "나쁜말");

    public PurgomalumFakeClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public boolean containsProfanity(String text) {
        return profanityList.stream()
                .anyMatch(text::contains);
    }
}
