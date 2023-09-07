package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class ProductFakeRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return products.stream()
            .filter(it -> ids.contains(it.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public Product save(Product entity) {
        products.add(entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return products.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findAny();
    }
}
