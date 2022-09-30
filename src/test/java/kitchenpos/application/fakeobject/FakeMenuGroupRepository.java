package kitchenpos.application.fakeobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private List<MenuGroup> menuGroupList;
    private ObjectMapper objectMapper = new ObjectMapper();

    public FakeMenuGroupRepository() {
        this.menuGroupList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            menuGroupList.add(MenuGroup.of(UUID.fromString("5e9879b7-6112-4791-a4ce-f22e94af875" + i), "test" + i));
        }
    }

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        if (menuGroup.getId() != null) {
            for (MenuGroup menuGroupItem : menuGroupList) {
                if (menuGroupItem.getId().equals(menuGroup.getId())) {
                    try {
                        menuGroupItem = objectMapper.readValue(objectMapper.writeValueAsString(menuGroup), MenuGroup.class);
                        return menuGroupItem;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        menuGroup.setId(UUID.randomUUID());
        menuGroupList.add(menuGroup);
        return menuGroup;
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroupList;
    }

    @Override
    public Optional<MenuGroup> findById(UUID menuGroupId) {
        for (MenuGroup menuGroup : menuGroupList) {
            if (menuGroup.getId().equals(menuGroupId)) {
                return Optional.of(menuGroup);
            }
        }
        return Optional.empty();
    }
}
