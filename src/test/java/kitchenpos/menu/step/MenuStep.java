package kitchenpos.menu.step;

import kitchenpos.menu.fixture.MenuProductSaveRequest;
import kitchenpos.menu.fixture.MenuSaveRequest;

import java.util.List;
import java.util.UUID;

public class MenuStep {

    public static MenuSaveRequest createMenuSaveRequest(final String name, boolean displayed, int price, UUID menuGroupId, List<MenuProductSaveRequest> menuProducts) {
        return MenuSaveRequest.builder()
                .name(name)
                .displayed(displayed)
                .price(price)
                .menuGroupId(menuGroupId)
                .menuProducts(menuProducts)
                .build();
    }
}
