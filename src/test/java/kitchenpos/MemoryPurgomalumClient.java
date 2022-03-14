package kitchenpos;

import kitchenpos.infra.PurgomalumClient;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class MemoryPurgomalumClient extends PurgomalumClient {

  public MemoryPurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
    super(Mockito.mock(RestTemplateBuilder.class));
  }

  public boolean containsProfanity(final String text) {
    return false;
  }
}
