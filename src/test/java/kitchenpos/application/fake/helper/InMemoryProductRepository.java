package kitchenpos.application.fake.helper;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> elements = new HashMap<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return elements.entrySet().stream()
                .filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public Product save(Product product) {
        elements.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(elements.values());
    }
}
