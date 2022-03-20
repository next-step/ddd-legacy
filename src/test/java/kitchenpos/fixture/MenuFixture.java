package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * kitchenpos.fixture
 *      MenuFixture
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-21 오전 1:25
 */

public class MenuFixture {

    public static Menu 메뉴() {
        UUID uuid = UUID.randomUUID();
        return new Menu(
            UUID.randomUUID(),
            "후라이드",
            BigDecimal.valueOf(16000),
            true, uuid,
            Collections.singletonList(
                MenuProductFixture.메뉴_상품(1L,
                    ProductFixture.상품(),
                    1,
                    uuid
                )
            )
        );
    }
}
