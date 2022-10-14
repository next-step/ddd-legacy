package kitchenpos.infra;

import java.net.URI;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PurgomalumClient implements ProfanityDetectClient {

    private static final String ENDPOINT = "https://www.purgomalum.com/service/containsprofanity";

    private final RestTemplate restTemplate;

    public PurgomalumClient(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public boolean containsProfanity(final String text) {
        final URI url = UriComponentsBuilder.fromUriString(ENDPOINT)
            .queryParam("text", text)
            .build()
            .toUri();
        return Boolean.parseBoolean(restTemplate.getForObject(url, String.class));
    }
}
