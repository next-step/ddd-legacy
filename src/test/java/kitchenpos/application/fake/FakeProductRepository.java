package kitchenpos.application.fake;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private final Map<UUID, Product> memoryMap = new HashMap<>();

    @Override
    public Product save(Product product) {
        memoryMap.put(product.getId(), product);

        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(memoryMap.get(productId));
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(memoryMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAll() {
        return Lists.newArrayList(memoryMap.values());
    }
}
