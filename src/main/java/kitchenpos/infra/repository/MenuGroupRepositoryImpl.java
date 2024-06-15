package kitchenpos.infra.repository;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class MenuGroupRepositoryImpl implements MenuGroupRepository {
    private final MenuGroupJpaRepository menuGroupJpaRepository;

    public MenuGroupRepositoryImpl(MenuGroupJpaRepository menuGroupJpaRepository) {
        this.menuGroupJpaRepository = menuGroupJpaRepository;
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return menuGroupJpaRepository.findById(id);
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroupJpaRepository.findAll();
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        return menuGroupJpaRepository.save(menuGroup);
    }
}
