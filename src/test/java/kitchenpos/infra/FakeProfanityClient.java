package kitchenpos.infra;

import java.util.List;
import kitchenpos.domain.ProfanityClient;

public class FakeProfanityClient implements ProfanityClient {

  private static final List<String> profanities = List.of("비속어", "욕설", "shit", "fuck");

  @Override
  public boolean containsProfanity(String text) {
    return profanities.stream()
        .anyMatch(it -> text.toLowerCase().contains(it));
  }
}
