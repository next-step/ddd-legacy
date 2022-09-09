package kitchenpos.application.fake;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeProductRepository implements ProductRepository {

    private Map<UUID, Product> fakeRepository = new HashMap<>();

    @Override
    public Product save(Product product) {
        if (fakeRepository.containsKey(product.getId())) {
            throw new IllegalArgumentException("duplicate primary key");
        }
        return fakeRepository.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(fakeRepository.get(id));
    }

    @Override
    public List<Product> findAll() {
        return fakeRepository.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return fakeRepository.values()
                .stream()
                .filter(product -> ids.contains(product.getId()))
                .collect(Collectors.toList());
    }
}
