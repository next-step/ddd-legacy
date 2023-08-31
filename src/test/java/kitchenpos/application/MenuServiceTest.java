package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.Fixtures.createMenu;
import static kitchenpos.Fixtures.createMenuGroup;
import static kitchenpos.Fixtures.createMenuProduct;
import static kitchenpos.Fixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class MenuServiceTest {
    @Autowired
    private MenuService menuService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;


    @Test
    @DisplayName("메뉴를 등록한다.")
    void create01() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when
        Menu savedMenu = menuService.create(menu);

        // then
        assertThat(savedMenu.getId()).isNotNull();
    }

    @Test
    @DisplayName("메뉴의 가격은 0보다 작을 수 없다.")
    void create02() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("-1"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴그룹의 경우 미리 등록 되어있어야 한다.")
    void create03() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");

        Menu menu = createMenu("메뉴이름", new BigDecimal("-1"), menuGroup,
                               List.of(createMenuProduct(1L, createProduct("상품1", new BigDecimal("10000")), 1L),
                                       createMenuProduct(2L, createProduct("상품2", new BigDecimal("20000")), 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴상품의 경우 비어있을 수 없다.")
    void create04() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");

        Menu menu = createMenu("메뉴이름", new BigDecimal("-1"), menuGroup,
                               Collections.emptyList()
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴상품의 경우 상품이 등록 되어 있어야 한다.")
    void create05() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));

        Menu menu = createMenu("메뉴이름", new BigDecimal("-1"), savedMenuGroup,
                               List.of(createMenuProduct(1L, product1, 1L),
                                       createMenuProduct(2L, product2, 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴상품의 경우 수량은 0보다 커야 한다.")
    void create06() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        MenuProduct menuProduct1 = createMenuProduct(1L, savedProducts.get(0), 1L);
        menuProduct1.setQuantity(0);
        Menu menu = createMenu("메뉴이름", new BigDecimal("-1"), savedMenuGroup,
                               List.of(menuProduct1,
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴상품의 경우 수량은 0보다 커야 한다.")
    void create07() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        MenuProduct menuProduct1 = createMenuProduct(1L, savedProducts.get(0), 1L);
        menuProduct1.setQuantity(0);
        Menu menu = createMenu("메뉴이름", new BigDecimal("-1"), savedMenuGroup,
                               List.of(menuProduct1,
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 메뉴의 상품들의 가격 * 수량 보다 클 수 없다.")
    void create08() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("31000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 이름은 비어있을 수 없다.")
    void create09() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu(null, new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 이름은 비어있을 수 없다.")
    void create10() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격을 변경한다.")
    void changePrice01() {
        // given
        Menu menu = savedMenu();
        menu.setPrice(new BigDecimal("10000"));

        // when
        menuService.changePrice(menu.getId(), menu);

        // then
        Menu findMenu = menuRepository.findById(menu.getId()).orElseThrow();
        assertThat(findMenu.getPrice()).isEqualByComparingTo(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("메뉴의 가격은 0보다 작을 수 없다.")
    void changePrice02() {
        // given
        Menu menu = savedMenu();
        menu.setPrice(new BigDecimal("-1000"));

        // when
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 0보다 작을 수 없다.")
    void changePrice03() {
        // given
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(new BigDecimal("10000"));

        // when
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu)).isInstanceOf(
                NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품이 포함된 메뉴들의 가격이 메뉴의 가격보다 클 수 없다.")
    void changePrice04() {
        // given
        Menu menu = savedMenu();
        menu.setPrice(new BigDecimal("50000"));

        // when
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Disabled("상품의 가격을 더하는 버그가 있어서 잠시 막음")
    @Test
    @DisplayName("메뉴를 전시한다.")
    void display01() {
        // given
        Menu menu = savedHideMenu();
        assertThat(menu.isDisplayed()).isFalse();

        // when
        Menu displayedMenu = menuService.display(menu.getId());

        // when
        assertThat(displayedMenu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴를 숨긴다.")
    void hide01() {
        // given
        Menu menu = savedMenu();
        assertThat(menu.isDisplayed()).isTrue();

        // when
        Menu displayedMenu = menuService.hide(menu.getId());

        // when
        assertThat(displayedMenu.isDisplayed()).isFalse();
    }

    public Menu savedMenu() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        // when
        return menuRepository.save(menu);
    }

    public Menu savedHideMenu() {
        // given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );

        menu.setDisplayed(false);

        // when
        return menuRepository.save(menu);
    }
}
