package kitchenpos.menu.menugroup.application;

import kitchenpos.common.vo.Name;
import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.menu.menugroup.dto.request.MenuGroupRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;
    private final PurgomalumClient purgomalumClient;

    public MenuGroupService(final MenuGroupRepository menuGroupRepository, PurgomalumClient purgomalumClient) {
        this.menuGroupRepository = menuGroupRepository;
        this.purgomalumClient = purgomalumClient;
    }

    @Transactional
    public MenuGroup create(final MenuGroupRequest request) {
        final MenuGroup menuGroup = new MenuGroup(UUID.randomUUID(), new Name(request.getName(), purgomalumClient.containsProfanity(request.getName())));
        return menuGroupRepository.save(menuGroup);
    }

    @Transactional(readOnly = true)
    public List<MenuGroup> findAll() {
        return menuGroupRepository.findAll();
    }
}
