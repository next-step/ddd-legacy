package kitchenpos.infra.repository.impl;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.infra.repository.MenuJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuRepositoryImpl implements MenuRepository {
    private final MenuJpaRepository menuJpaRepository;

    public MenuRepositoryImpl(MenuJpaRepository menuJpaRepository) {
        this.menuJpaRepository = menuJpaRepository;
    }

    @Override
    public Menu save(Menu menu) {
        return menuJpaRepository.save(menu);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return menuJpaRepository.findById(id);
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
    public List<Menu> findAll() {
        return menuJpaRepository.findAll();
    }
}
