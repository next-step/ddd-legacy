package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.Collections;
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

    public static Menu 메뉴_가격_0원_미만() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(-5000L));
        return menu;
    }

    public static Menu 등록된_메뉴_요청() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드+후라이드");
        menu.setPrice(BigDecimal.valueOf(19000));
        menu.setMenuGroupId(UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580"));
        menu.setDisplayed(false);
        menu.setMenuProducts(Collections.singletonList(MenuProductFixture.메뉴_상품(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"), 2)));
        return menu;
    }

    public static Menu 노출중인_메뉴() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드+후라이드");
        menu.setPrice(BigDecimal.valueOf(19000));
        menu.setMenuGroupId(UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580"));
        menu.setDisplayed(true);
        menu.setMenuProducts(Collections.singletonList(MenuProductFixture.메뉴_상품(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"), 2)));
        return menu;
    }

    public static Menu 정상_메뉴_요청() {
        Menu menu = new Menu();
        menu.setName("후라이드+후라이드");
        menu.setPrice(BigDecimal.valueOf(19000));
        menu.setMenuGroupId(UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580"));
        menu.setDisplayed(true);
        menu.setMenuProducts(Collections.singletonList(MenuProductFixture.메뉴_상품(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"), 2)));
        return menu;
    }

    public static Menu 빈_메뉴_이름() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(19000));
        menu.setMenuGroupId(UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580"));
        menu.setDisplayed(true);
        menu.setMenuProducts(Collections.singletonList(MenuProductFixture.메뉴_상품(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"), 2)));
        return menu;
    }
    
    public static Menu 비속어_메뉴_이름() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(19000));
        menu.setMenuGroupId(UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580"));
        menu.setDisplayed(true);
        menu.setMenuProducts(Collections.singletonList(MenuProductFixture.메뉴_상품(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"), 2)));
        return menu;
    }
    

    public static Menu 빈_메뉴_그룹() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(16000L));
        return menu;
    }

    public static Menu 빈_메뉴_상품(MenuGroup menuGroup) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(16000L));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    public static Menu 메뉴() {
        UUID uuid = UUID.randomUUID();
        return new Menu(
                uuid,
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
