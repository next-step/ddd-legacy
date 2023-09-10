package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixure;
import kitchenpos.util.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuServiceTest extends ServiceTest {

    private final MenuService menuService;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;
    private final ProductRepository productRepository;

    private Product product;
    private MenuGroup menuGroup;

    public MenuServiceTest(final MenuService menuService,
                           final MenuGroupRepository menuGroupRepository,
                           final MenuRepository menuRepository,
                           final ProductRepository productRepository) {
        this.menuService = menuService;
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
        this.productRepository = productRepository;
    }

    @BeforeEach
    void setup() {
        this.product = productRepository.save(ProductFixure.create("양념치킨", 1000));
        this.menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create() {
        menuService.create(MenuFixture.create(menuGroup, product));
    }

    @DisplayName("메뉴를 생성시 음수이면 에러를 반환한다.")
    @Test
    void createNegativePriceException() {
        assertThatThrownBy(() -> menuService.create(MenuFixture.create(menuGroup, product, -1000)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성시, 상품이 존재하지 않으면 에러를 반환한다.")
    @Test
    void createEmptyProductException() {
        Product product = ProductFixure.create("양념치킨", 1000);

        assertThatThrownBy(() -> menuService.create(MenuFixture.create(menuGroup, product, 1000)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성시, 수량이 0 미만이면, 에러를 반환한다.")
    @Test
    void createNegativeQuntityException() {
        MenuProduct menuProduct = MenuProductFixture.create(product, -1);
        Menu menu = MenuFixture.create("치킨", menuGroup, menuProduct, 1000);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시, 상품의 총합이 메뉴의 가격보다 클경우 에러를 반환한다.")
    @Test
    void createMenuPriceComapreTotalProductprice() {
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
        Product product = productRepository.save(ProductFixure.create("양념치킨", 500));
        MenuProduct menuProduct = MenuProductFixture.create(product, 1);
        Menu menu = MenuFixture.create("치킨", menuGroup, menuProduct, 1000);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시, 메뉴 이름이 비속어이면 에러를 반환한다.")
    @Test
    void createnamePurgomalException() {
        MenuProduct menuProduct = MenuProductFixture.create(product, 1);
        Menu menu = MenuFixture.create("bitch", menuGroup, menuProduct, 1000);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출 시킬수 있다.")
    @Test
    void display() {
        Menu menu = menuService.create(MenuFixture.create(menuGroup, product));

        boolean isDisplayed = menuService.display(menu.getId()).isDisplayed();

        assertThat(isDisplayed).isTrue();
    }

    @DisplayName("메뉴를 비노출 시킬수 있다.")
    @Test
    void hide() {
        Menu menu = menuService.create(MenuFixture.create(menuGroup, product));

        boolean isDisplayed = menuService.hide(menu.getId()).isDisplayed();

        assertThat(isDisplayed).isFalse();
    }

}
