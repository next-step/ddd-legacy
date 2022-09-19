package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  Product save(Product product);

  Optional<Product> findById(UUID id);

  List<Product> findAllByIdIn(List<UUID> ids);

  List<Product> findAll();

  void deleteAll();

  void saveAll(List<Product> products);
}
