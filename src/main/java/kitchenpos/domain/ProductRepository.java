package kitchenpos.domain;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends BaseRepository<Product, UUID> {
    List<Product> findAllByIdIn(List<UUID> ids);
}
