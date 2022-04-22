package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.stream.Collectors;

public interface ProductRepository {
    List<Product> findAllByIdIn(List<UUID> ids);
    Product save(Product product);
    Optional<Product> findById(UUID uuid);
    List<Product> findAll();

}

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID>{
}