package kitchenpos.common;

import java.util.UUID;

public interface UuidBuilder {
  UUID createRandomUUID(String name);

  UUID createFixedUUID();
}
