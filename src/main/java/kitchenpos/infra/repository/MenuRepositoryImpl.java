package kitchenpos.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import org.springframework.stereotype.Repository;

@Repository
class MenuRepositoryImpl implements MenuRepository {
    private final MenuJpaRepository menuJpaRepository;

    public MenuRepositoryImpl(MenuJpaRepository menuJpaRepository) {
        this.menuJpaRepository = menuJpaRepository;
    }

    @Override
    public Menu save(Menu menu) {
        return menuJpaRepository.save(menu);
    }

    @Override
    public List<Menu> findAll() {
        return menuJpaRepository.findAll();
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menuJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menuJpaRepository.findAllByProductId(productId);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return menuJpaRepository.findById(id);
    }
}
