package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

/**
 * <pre>
 * kitchenpos.fixture
 *      MenuGroupFixture
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-21 오전 1:35
 */

public class MenuGroupFixture {

    public static MenuGroup 메뉴_그룹(UUID uuid, String name) {
        return new MenuGroup(uuid, name);
    }
}
