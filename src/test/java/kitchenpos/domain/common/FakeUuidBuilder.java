package kitchenpos.domain.common;

import kitchenpos.common.UuidBuilder;

import java.util.HashMap;
import java.util.UUID;

public class FakeUuidBuilder implements UuidBuilder {
  public final String SIMON = "52588691-d763-45fe-8de6-8a632e08384a";
  public final String UDON = "52588691-d763-45fe-8de6-8a632e08384b";
  public final String RAMEN = "52588691-d763-45fe-8de6-8a632e08384c";

  public static final HashMap<String, String> uuids = new HashMap<>();

  public FakeUuidBuilder(){
    uuids.put("SIMON", SIMON);
    uuids.put("UDON", UDON);
    uuids.put("RAMEN", RAMEN);
  }

  @Override
  public UUID createRandomUUID(String name) {
    return UUID.fromString(uuids.getOrDefault(name, "SIMON"));
  }
  @Override
  public UUID createFixedUUID() {
    return UUID.fromString(uuids.get("SIMON"));
  }
}
