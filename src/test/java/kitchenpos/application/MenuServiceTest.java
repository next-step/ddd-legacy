package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.AdditionalAnswers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Autowired
    @MockBean
    MenuRepository menuRepository;

    @Autowired
    @MockBean
    MenuGroupRepository menuGroupRepository;

    @Autowired
    @MockBean
    ProductRepository productRepository;

    @Autowired
    @MockBean
    PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    @DisplayName("메뉴를 등록할 수 있습니다.")
    @Test
    void crate() {
        final String menuName = "양념 후라이드 세트";
        final MenuGroup menuGroup = menuGroup();
        final Product firstProduct = product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000"));
        final Product secondProduct = product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000"));
        final Menu menu = menu(null, menuName, new BigDecimal("30000"),
                menuGroup, List.of(
                        menuProduct(1L, 1L, firstProduct),
                        menuProduct(2L, 1L, secondProduct)
                ), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
        when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
        when(purgomalumClient.containsProfanity(menuName)).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).then(AdditionalAnswers.returnsFirstArg());

        final Menu actual = menuService.create(menu);
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menuName),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(new BigDecimal("30000")),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menuGroup),
                () -> assertThat(actual.getMenuProducts()).hasSize(2),
                () -> assertThat(actual.isDisplayed()).isTrue()
        );
    }

    @DisplayName("메뉴 가격은 0원 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"-1", "-1000", "-100000"})
    void createWithNegativePrice(String price) {
        final Menu menu = menu(null, "양념 후라이드 세트", new BigDecimal(price),
                menuGroup(), List.of(menuProduct()), true
        );
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹이 존재하지 않으면 메뉴를 등록할 수 없습니다.")
    @Test
    void createWithNotExistsMenuGroup() {
        final Menu menu = menu(null, "양념 후라이드 세트", new BigDecimal("30000"),
                menuGroup(), List.of(menuProduct()), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 상품이 없거나 비어있으면 메뉴를 등록할 수 없습니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @NullAndEmptySource
    void createWithEmptyMenuProducts(List<MenuProduct> menuProducts) {
        final MenuGroup menuGroup = menuGroup();
        final Menu menu = menu(null, "양념 후라이드 세트", new BigDecimal("30000"),
                menuGroup, menuProducts, true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매뉴 상품의 갯수와 상품의 갯수가 다르면 메뉴를 등록할 수 없습니다.")
    @Test
    void createWithDifferentMenuProductSize() {
        final MenuGroup menuGroup = menuGroup();
        final Menu menu = menu(null, "양념 후라이드 세트", new BigDecimal("30000"),
                menuGroup, List.of(menuProduct()), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매뉴 상품의 수량이 0보다 작으면 메뉴를 등록할 수 없습니다.")
    @Test
    void createWithNegativeMenuProductQuantity() {
        final MenuGroup menuGroup = menuGroup();
        final Product product = product();
        final Menu menu = menu(null, "양념 후라이드 세트", new BigDecimal("30000"),
                menuGroup, List.of(menuProduct(1L, -1L, product)), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 가격 합보다 크면 메뉴를 등록할 수 없습니다.")
    @Test
    void createWithPriceLessThanSumOfMenuProductPrice() {
        final MenuGroup menuGroup = menuGroup();
        final Product firstProduct = product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000"));
        final Product secondProduct = product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000"));
        final Menu menu = menu(null, "양념 후라이드 세트", new BigDecimal("32010"),
                menuGroup, List.of(
                        menuProduct(1L, 1L, firstProduct),
                        menuProduct(2L, 1L, secondProduct)
                ), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
        when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 없거나 비어있으면 메뉴를 등록할 수 없습니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @NullAndEmptySource
    void createWithEmptyOrBlankMenuName(final String name) {
        final MenuGroup menuGroup = menuGroup();
        final Product firstProduct = product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000"));
        final Product secondProduct = product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000"));
        final Menu menu = menu(null, name, new BigDecimal("30000"),
                menuGroup, List.of(
                        menuProduct(1L, 1L, firstProduct),
                        menuProduct(2L, 1L, secondProduct)
                ), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
        when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 욕설이 포함되어 있으면 메뉴를 등록할 수 없습니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"비속어", "욕설", "그XX"})
    void createWithEmptyOrProfanityMenuName(final String name) {
        final MenuGroup menuGroup = menuGroup();
        final Product firstProduct = product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000"));
        final Product secondProduct = product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000"));
        final Menu menu = menu(null, name, new BigDecimal("30000"),
                menuGroup, List.of(
                        menuProduct(1L, 1L, firstProduct),
                        menuProduct(2L, 1L, secondProduct)
                ), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
        when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
        when(purgomalumClient.containsProfanity(name)).thenReturn(true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
