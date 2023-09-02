package kitchenpos.infra;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MenuGroupRepositoryImpl implements MenuGroupRepository {

    private JpaMenuGroupRepository jpaMenuGroupRepository;

    public MenuGroupRepositoryImpl(JpaMenuGroupRepository jpaMenuGroupRepository) {
        this.jpaMenuGroupRepository = jpaMenuGroupRepository;
    }

    @Override
    public MenuGroup save(MenuGroup entity) {
        return jpaMenuGroupRepository.save(entity);
    }

    @Override
    public List<MenuGroup> findAll() {
        return jpaMenuGroupRepository.findAll();
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return jpaMenuGroupRepository.findById(id);
    }
}
