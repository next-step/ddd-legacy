package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuGroupRepository;
import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeProductRepository;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final ProductRepository productRepository = new FakeProductRepository();
    private final PurgomalumClient purgomalumClient = new PurgomalumClient(new RestTemplateBuilder());

    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);


    private UUID productId;
    private MenuGroup menuGroup;
    private List<MenuProduct> menuProducts = new ArrayList<>();

    @BeforeEach
    void init() {
        Product product = productRepository.save(product());
        productId = product.getId();
        MenuProduct menuProduct = menuProduct(product, 2);
        menuProducts.add(menuProduct);
        menuGroup = menuGroupRepository.save(menuGroup("메뉴 그룹"));
    }

    @Test
    @DisplayName("메뉴를 생성하여 저장한다.")
    void createMenu() {
        // given
        Menu menu = new Menu();
        menu.setName("메뉴");
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(15000));

        // when
        Menu saveMenu = menuService.create(menu);

        // then
        assertAll(
                () -> assertThat(saveMenu.getId()).isNotNull(),
                () -> assertThat(saveMenu.getName()).isEqualTo("메뉴")
        );
    }

    @Test
    @DisplayName("메뉴의 가격을 수정한다.")
    void changeMenuPrice() {
        // given
        Menu menu = createMenu("메뉴");
        Menu changePrice = new Menu();
        BigDecimal price = BigDecimal.valueOf(14000);
        changePrice.setPrice(price);

        // when
        Menu saveMenu = menuService.changePrice(menu.getId(), changePrice);

        // then
        assertAll(
                () -> assertThat(saveMenu.getId()).isNotNull(),
                () -> assertThat(saveMenu.getName()).isEqualTo("메뉴"),
                () -> assertThat(saveMenu.getPrice()).isEqualTo(price)
        );
    }

    @Test
    @DisplayName("메뉴를 전시한다.")
    void displayMenu() {
        // given
        Menu menu = createMenu("전시 메뉴");

        // when
        Menu saveMenu = menuService.display(menu.getId());

        // then
        assertThat(saveMenu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴를 숨긴다.")
    void hideMenu() {
        // given
        Menu menu = createMenu("전시 메뉴");

        // when
        Menu saveMenu = menuService.hide(menu.getId());

        // then
        assertThat(saveMenu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("메뉴리스트를 가져온다.")
    void findMenus() {
        // given
        createMenu("메뉴 1");
        createMenu("메뉴 2");
        createMenu("메뉴 3");

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertAll(
                () -> assertThat(menus).isNotEmpty(),
                () -> assertThat(menus.size()).isEqualTo(3)
        );
    }

    private Menu createMenu(String name) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(15000));
        return menuService.create(menu);
    }
}
