package kitchenpos.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class InMemoryProductRepository extends InMemoryCrudRepository<Product, UUID> implements ProductRepository {
    @Override
    UUID selectId(Product entity) {
        return entity.getId();
    }


    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(storage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
