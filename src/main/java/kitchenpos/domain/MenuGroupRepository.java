package kitchenpos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuGroupRepository  {
    MenuGroup save(MenuGroup entity);

    Optional<MenuGroup> findById(UUID id);

    List<MenuGroup> findAll();

    List<MenuGroup> findAllByIdIn(List<UUID> ids);

}
