package kitchenpos.domain.testfixture;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductFakeRepository implements ProductRepository {

    InstancePocket<UUID, Product> instancePocket;

    public ProductFakeRepository() {
        this.instancePocket = new InstancePocket<>();
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return instancePocket.findAllByIdIn(ids);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return instancePocket.findById(id);
    }

    @Override
    public Product save(Product product) {
        return instancePocket.save(product);
    }

    @Override
    public List<Product> findAll() {
        return instancePocket.findAll();
    }
}
