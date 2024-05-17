package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository {
    void saveAll(List<MenuGroup> menuGroups);

    Optional<MenuGroup> findById(UUID menuGroupId);

    MenuGroup save(MenuGroup menuGroup);

    List<MenuGroup> findAll();
}
