package kitchenpos.ui;

import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuGroupFixture.추천메뉴;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;

public class FakeMenuGroupService implements MenuGroupService {

    @Override
    public MenuGroup create(MenuGroup request) {
        request.setId(UUID.randomUUID());
        return request;
    }

    @Override
    public List<MenuGroup> findAll() {
        return Arrays.asList(세트메뉴, 추천메뉴);
    }
}
