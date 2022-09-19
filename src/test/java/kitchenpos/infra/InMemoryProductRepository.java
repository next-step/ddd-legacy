package kitchenpos.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class InMemoryProductRepository implements ProductRepository {

  private static final Map<UUID, Product> store = new HashMap<>();

  @Override
  public Product save(Product product) {
    store.put(product.getId(), product);
    return product;
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Product> findAllByIdIn(List<UUID> ids) {
    return ids.stream()
        .map(it -> store.get(it))
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findAll() {
    return new ArrayList<>(store.values());
  }
}
