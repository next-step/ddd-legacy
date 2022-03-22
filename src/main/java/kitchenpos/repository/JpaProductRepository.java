package kitchenpos.repository;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaProductRepository extends ProductRepository, JpaRepository<Product, UUID> {

}
