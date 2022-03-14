package kitchenpos;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class MemoryPurgomalumClient extends PurgomalumClient {

  public MemoryPurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
    super(restTemplateBuilder);
  }

  public boolean containsProfanity(final String text) {
    return false;
  }
}
