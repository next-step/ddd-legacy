package kitchenpos.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.db.MenuJpaRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import org.springframework.stereotype.Service;

@Service
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;

    public MenuRepositoryImpl(MenuJpaRepository menuJpaRepository) {
        this.menuJpaRepository = menuJpaRepository;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menuJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return menuJpaRepository.findById(id);
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menuJpaRepository.findAllByProductId(productId);
    }

    @Override
    public Menu save(Menu entity) {
        return menuJpaRepository.save(entity);
    }

    @Override
    public List<Menu> findAll() {
        return menuJpaRepository.findAll();
    }
}
