package kitchenpos.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuGroupRepository {
  MenuGroup save(MenuGroup menuGroup);
  List<MenuGroup> findAll();
  Optional<MenuGroup> findById(UUID id);
}
