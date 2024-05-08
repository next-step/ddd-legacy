package kitchenpos.menu;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.menu.fixture.MenuFixture;
import kitchenpos.menu.fixture.MenuGroupFixture;
import kitchenpos.menu.fixture.MenuProductFixture;
import kitchenpos.menu.fixture.ProductFixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuTestHelper {
    public static MenuProduct 메뉴_떡볶이 = MenuProductFixture.create(ProductFixture.떡볶이,10);
    public static Menu 메뉴 = MenuFixture.create(
            UUID.randomUUID(), "메뉴", new BigDecimal(10000),
            MenuGroupFixture.메뉴_그룹_한식, true, List.of(메뉴_떡볶이)
    );
}
