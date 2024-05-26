package kitchenpos.application.product;

import kitchenpos.common.UuidBuilder;

import java.util.UUID;

public class FakeUuidBuilder implements UuidBuilder {
  public static final String SIMON = "52588691-d763-45fe-8de6-8a632e08384a";
  @Override
  public UUID createRandomUUID() {
    return UUID.fromString(SIMON);
  }
}
