package kitchenpos.infra;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.UUID;

@RepositoryDefinition(domainClass = Product.class, idClass = UUID.class)
public interface JpaProductRepository extends ProductRepository, JpaSpecificationExecutor<Product> {
    List<Product> findAllByIdIn(List<UUID> ids);
}
