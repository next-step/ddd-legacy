package kitchenpos.integration;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.integration.mock.MemoryMenuGroupRepository;
import kitchenpos.integration.mock.MemoryMenuRepository;
import kitchenpos.integration.mock.MemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static kitchenpos.unit.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
class MenuServiceTest {

    @Autowired
    MenuService menuService;

    @Autowired
    MemoryMenuRepository menuRepository;

    @Autowired
    MemoryMenuGroupRepository menuGroupRepository;

    @Autowired
    MemoryProductRepository productRepository;

    @BeforeEach
    void setUp() {
        menuRepository.clear();
        menuGroupRepository.clear();
        productRepository.clear();
    }

    @DisplayName("메뉴의 가격은 0원보다 작을 수 없다.")
    @Test
    void create_Illegal_NegativePrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu request = aMenu("후라이드 치킨", -10_000, menuGroup, menuProduct);

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 존재하지 않는 메뉴 그룹에 포함될 수 없다.")
    @Test
    void create_Illegal_MenuGroup() {
        // given
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu request = aMenu("후라이드 치킨", 10_000, aMenuGroup(), menuProduct);

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 상품들을 포함해야한다.")
    @Test
    void create_Illegal_EmptyMenuProducts() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());

        Menu request = aMenu("후라이드 치킨", 10_000);
        request.setMenuGroupId(menuGroup.getId());

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함된 상품들은 존재하는 상품이어야 한다.")
    @Test
    void create_Illegal_NotExistingProducts() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu request = aMenu("후라이드 치킨", 30_000, menuGroup, menuProduct);

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴상품 가격의 총합보다 클 수 없다.")
    @Test
    void create_Illegal_TooMuchPrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu request = aMenu("후라이드 치킨", 30_000, menuGroup, menuProduct);

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu request = aMenu("후라이드 치킨", 10_000, menuGroup, menuProduct);

        // when
        Menu saved = menuService.create(request);

        // then
        assertThat(saved.getId()).isNotNull();
    }

    @DisplayName("변경하려는 메뉴의 가격은 0원 이상이어야 한다.")
    @Test
    void changePrice_Illegal_NegativePrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu menu = aMenu("후라이드 치킨", 10_000, menuGroup, menuProduct);
        menuRepository.save(menu);

        // when + then
        Menu changeRequest = new Menu();
        changeRequest.setPrice(BigDecimal.valueOf(-10_000));
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경하려는 메뉴의 가격은 메뉴 상품들의 총합보다 클 수 없다.")
    @Test
    void changePrice_Illegal_TooMuchPrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu menu = aMenu("후라이드 치킨", 10_000, menuGroup, menuProduct);
        menuRepository.save(menu);

        // when + then
        Menu changeRequest = new Menu();
        changeRequest.setPrice(BigDecimal.valueOf(30_000));
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경")
    @Test
    void changePrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu menu = aMenu("후라이드 치킨", 10_000, menuGroup, menuProduct);
        menuRepository.save(menu);

        // when
        Menu changeRequest = new Menu();
        changeRequest.setPrice(BigDecimal.valueOf(9_000));
        Menu changedMenu = menuService.changePrice(menu.getId(), changeRequest);

        // then
        assertThat(changedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(9_000));
    }

    @DisplayName("가격 정책에 맞지 않는 메뉴는 진열할 수 없다.")
    @Test
    void display_Illegal_TooMuchPrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu menu = aMenu("후라이드 치킨", 20_000, menuGroup, menuProduct);
        menuRepository.save(menu);

        // when + then
        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("가격 정책에 맞는 메뉴는 진열할 수 있다.")
    @Test
    void display() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroup());
        Product product = aProduct("후라이드 치킨", 10_000);
        productRepository.save(product);
        MenuProduct menuProduct = aMenuProduct(product, 1);

        Menu menu = aMenu("후라이드 치킨", 10_000, menuGroup, menuProduct);
        menu.setDisplayed(false);
        menuRepository.save(menu);

        // when
        Menu displayedMenu = menuService.display(menu.getId());

        // then
        assertThat(displayedMenu.isDisplayed()).isTrue();
    }
}
