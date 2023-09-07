package kitchenpos.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.db.MenuGroupJpaRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.stereotype.Service;

@Service
public class MenuGroupRepositoryImpl implements MenuGroupRepository {

    private final MenuGroupJpaRepository menuGroupJpaRepository;

    public MenuGroupRepositoryImpl(MenuGroupJpaRepository menuGroupJpaRepository) {
        this.menuGroupJpaRepository = menuGroupJpaRepository;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menuGroupJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menuGroupJpaRepository.findAllByProductId(productId);
    }

    @Override
    public MenuGroup save(MenuGroup entity) {
        return menuGroupJpaRepository.save(entity);
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroupJpaRepository.findAll();
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        return menuGroupJpaRepository.findById(menuGroupId);
    }
}
