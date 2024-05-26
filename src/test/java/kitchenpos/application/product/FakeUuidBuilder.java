package kitchenpos.application.product;

import kitchenpos.common.UuidBuilder;

import java.util.UUID;

public class FakeUuidBuilder implements UuidBuilder {
  public static final String SIMON = "SIMON";

  @Override
  public UUID createRandomUUID() {
    return UUID.fromString(SIMON);
  }
}
