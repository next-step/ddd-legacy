package kitchenpos.application.fake.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class InMemoryProductRepository implements ProductRepository {
  private final Map<UUID, Product> maps = new ConcurrentHashMap<>();
  @Override
  public Product save(Product product) {
    maps.put(product.getId(), product);
    return product;
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return Optional.ofNullable(maps.get(id));
  }

  @Override
  public List<Product> findAll() {
    return new ArrayList<>(maps.values());
  }

  @Override
  public List<Product> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(maps::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
