package kitchenpos.infra;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public interface PurgomalumClient {
    boolean containsProfanity(final String text);
}

@Component
class PurgomalumRestClient implements PurgomalumClient {
    private final RestTemplate restTemplate;

    public PurgomalumRestClient(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public boolean containsProfanity(final String text) {
        final URI url = UriComponentsBuilder.fromUriString("https://www.purgomalum.com/service/containsprofanity")
            .queryParam("text", text)
            .build()
            .toUri();
        return Boolean.parseBoolean(restTemplate.getForObject(url, String.class));
    }
}
