package kitchenpos.repository;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {
    private final BaseInMemoryDao<Product> dao = new BaseInMemoryDao<>();

    @Override
    public Product save(Product entity) {
        return dao.save(entity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return dao.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        return dao.findAllByIdIn(ids);
    }
}
