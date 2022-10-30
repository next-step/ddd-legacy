package kitchenpos.menu.menu.application;

import kitchenpos.menu.menu.MenuFixture;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.menu.menu.MenuFixture.menuProducts;
import static kitchenpos.menu.menu.MenuFixture.메뉴가격이_메뉴상품합_보다큼;
import static kitchenpos.menu.menugroup.MenuGroupFixture.menuGroup;
import static kitchenpos.product.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("메뉴 전시 서비스")
class MenuDisplayServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuDisplayService menuDisplayService;

    private static Product product;
    private Menu menu;
    private Menu 메뉴가격이_메뉴상품합_보다큼;

    @BeforeEach
    void setUp() {
        product = productRepository.save(product(UUID.randomUUID(), BigDecimal.ONE));
        MenuGroup 메뉴그룹 = menuGroupRepository.save(menuGroup());
        menu = menuRepository.save(MenuFixture.menu(메뉴그룹, menuProducts(product.getId())));
        메뉴가격이_메뉴상품합_보다큼 = menuRepository.save(메뉴가격이_메뉴상품합_보다큼(메뉴그룹, menuProducts(product.getId())));
    }

    @DisplayName("메뉴를 보일 수 있다.")
    @Test
    void display() {
        assertThat(menu.isDisplayed()).isTrue();
        menuDisplayService.hide(menu.getId());
        assertThat(menu.isDisplayed()).isFalse();
        menuDisplayService.display(menu.getId());
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.")
    @Test
    void displaysd() {
        assertThat(메뉴가격이_메뉴상품합_보다큼.isDisplayed()).isTrue();
        menuDisplayService.hide(메뉴가격이_메뉴상품합_보다큼.getId());
        assertThat(메뉴가격이_메뉴상품합_보다큼.isDisplayed()).isFalse();
        assertThatThrownBy(() -> menuDisplayService.display(메뉴가격이_메뉴상품합_보다큼.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.");
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        assertThat(menu.isDisplayed()).isTrue();
        menuDisplayService.hide(menu.getId());
        assertThat(menu.isDisplayed()).isFalse();
    }
}
