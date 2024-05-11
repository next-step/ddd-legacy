package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Nested
    class createNested {
        @DisplayName("메뉴를 생성할 수 있다.")
        @Test
        void createSuccessTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            Menu createdMenu = menuService.create(menu);

            assertThat(createdMenu.getId()).isNotNull();
        }

        @Test
        @DisplayName("메뉴의 가격이 존재하지 않은 경우 예외가 발생한다.")
        void createFailWhenPriceIsNullTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 0보다 작은 경우 예외가 발생한다.")
        void createFailWhenPriceIsNegativeTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(-16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품이 존재하지 않은 경우 예외가 발생한다.")
        void createFailWhenMenuProductIsEmptyTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, Collections.emptyList());

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품에서 상품이 존재하지 않은 경우 예외가 발생한다.")
        void createFailWhenProductIsEmptyTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            MenuProduct menuProduct = createMenuProduct(UUID.randomUUID(), 1);
            
            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품의 수량이 0보다 작은 경우 예외가 발생한다.")
        void createFailWhenQuantityIsNegativeTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), -1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 상품의 가격의 합보다 큰 경우 예외가 발생한다.")
        void createFailWhenPriceIsGreaterThanSumOfProductPriceTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(32000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴에 이름에 비속어가 포함된 경우 예외가 발생한다.")
        @Test
        void createFailWhenNameContainsProfanityTest() {
            given(purgomalumClient.containsProfanity("시발 후라이드치킨")).willReturn(true);
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "시발 후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름이 존재하지 않은 경우에 예외가 발생한다.")
        @Test
        void createFailWhenNameIsNullTest() {
            MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    class changePriceNested {
    }

    @Nested
    class displayNested {
    }

    @Nested
    class hide {
    }

    @Nested
    class findAll {
    }
}
