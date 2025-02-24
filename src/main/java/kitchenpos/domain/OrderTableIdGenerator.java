package kitchenpos.domain;

import java.util.UUID;

public interface OrderTableIdGenerator {
    UUID generateId();
}
