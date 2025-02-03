package kitchenpos.infra.repository.impl;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.infra.repository.MenuGroupJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuGroupRepositoryImpl implements MenuGroupRepository {
    private final MenuGroupJpaRepository menuGroupJpaRepository;

    public MenuGroupRepositoryImpl(final MenuGroupJpaRepository menuGroupJpaRepository) {
        this.menuGroupJpaRepository = menuGroupJpaRepository;
    }

    @Override
    public MenuGroup save(final MenuGroup menuGroup) {
        return menuGroupJpaRepository.save(menuGroup);
    }

    @Override
    public Optional<MenuGroup> findById(final UUID id) {
        return menuGroupJpaRepository.findById(id);
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroupJpaRepository.findAll();
    }
}
