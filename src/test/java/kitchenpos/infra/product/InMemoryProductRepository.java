package kitchenpos.infra.product;

import java.util.*;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

public class InMemoryProductRepository implements ProductRepository {

  private final Map<UUID, Product> memory = new HashMap<>();

  @Override
  public List<Product> findAllByIdIn(List<UUID> ids) {
    final List<Product> products = new ArrayList<>();

    for (UUID id : ids) {
      products.add(memory.get(id));
    }

    return products;
  }

  @Override
  public Product save(Product product) {
    memory.put(product.getId(), product);
    return product;
  }

  @Override
  public Optional<Product> findById(UUID productId) {
    return Optional.ofNullable(memory.get(productId));
  }

  @Override
  public List<Product> findAll() {
    return memory.values().stream().toList();
  }
}
