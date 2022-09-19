package kitchenpos.fakeobject;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryProductRepository extends AbstractInMemoryRepository<UUID, Product> implements ProductRepository {

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return super.maps.values()
                .stream()
                .filter(product -> ids.contains(product.getId()))
                .collect(Collectors.toList());
    }
}
