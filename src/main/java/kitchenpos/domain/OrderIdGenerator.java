package kitchenpos.domain;

import java.util.UUID;

public interface OrderIdGenerator {
    UUID generateId();
}
