package kitchenpos.application.fakeobject;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.math.BigDecimal;
import java.util.*;

public class FakeProductRepository implements ProductRepository {
    private Map<UUID, Product> productMap = new HashMap<>();

    public FakeProductRepository() {
        for (int i = 1; i <= 5; i++) {
            Product product = new Product();
            UUID id = UUID.fromString("0ac16db7-1b02-4a87-b9c1-e7d8f226c48" + i);
            product.setId(id);
            product.setPrice(BigDecimal.valueOf(1000 * i));
            productMap.put(id, product);
        }
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        List<Product> result = new ArrayList<>();
        for (UUID id : ids) {
            if (productMap.containsKey(id)) {
                result.add(productMap.get(id));
            }
        }
        return result;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        if (productMap.containsKey(productId)) {
            return Optional.of(productMap.get(productId));
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public Product save(Product product) {
        if (product.getId() != null && productMap.containsKey(product.getId())) {
            productMap.put(product.getId(), product);
            return product;
        }
        product.setId(UUID.randomUUID());
        productMap.put(product.getId(), product);
        return product;
    }
}
