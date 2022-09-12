package kitchenpos;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public Product save(Product product) {

        if (Objects.isNull(product.getId())) {
            product.setId(UUID.randomUUID());
            products.put(product.getId(), product);
        } else {
            products.put(product.getId(), product);
        }

        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
