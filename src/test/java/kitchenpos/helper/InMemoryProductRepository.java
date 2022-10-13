package kitchenpos.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> productIds) {
        return productIds.stream()
            .filter(products::containsKey)
            .map(products::get)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAll() {
        return products.values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }
}
