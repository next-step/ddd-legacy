package kitchenpos.application;

import com.sun.tools.javac.util.List;
import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.MenuGroupBuilder;
import kitchenpos.builder.MenuProductBuilder;
import kitchenpos.builder.ProductBuilder;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.mock.MockMenuGroupRepository;
import kitchenpos.mock.MockMenuRepository;
import kitchenpos.mock.MockProductRepository;
import kitchenpos.mock.MockPurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuServiceTest {
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = new MockMenuRepository();
        menuGroupRepository = new MockMenuGroupRepository();
        productRepository = new MockProductRepository();
        purgomalumClient = new MockPurgomalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("이름, 가격, 포함될 메뉴 그룹의 식별자, 메뉴 공개여부 및 포함할 제품 정보(식별자, 수량) 목록으로 메뉴를 추가한다")
    @Test
    void create() {
        final Product product = productRepository.save(ProductBuilder.newInstance().build());
        final MenuProduct menuProduct = MenuProductBuilder.newInstance().setProduct(product).build();
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        final Menu actual = menuService.create(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(menuRepository.findById(actual.getId())).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
                () -> assertThat(actual.getMenuGroup().getId()).isEqualTo(menuGroup.getId()),
                () -> assertThat(actual.getMenuProducts()).isNotEmpty()
        );
    }

    @DisplayName("가격은 필수고, 0 이상이어야 한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-16000")
    void create(final BigDecimal price) {
        final Product product = productRepository.save(ProductBuilder.newInstance().build());
        final MenuProduct menuProduct = MenuProductBuilder.newInstance().setProduct(product).build();
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setPrice(price)
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포함될 메뉴 그룹의 식별자로 특정 메뉴 그룹을 조회할 수 있어야 한다")
    @Test
    void createNoSuchMenuGroup() {
        final Product product = productRepository.save(ProductBuilder.newInstance().build());
        final MenuProduct menuProduct = MenuProductBuilder.newInstance().setProduct(product).build();
        final MenuGroup menuGroup = MenuGroupBuilder.newInstance().build();
        final Menu expected = MenuBuilder.newInstance()
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("포함할 제품 정보 목록은 필수다")
    @ParameterizedTest
    @NullSource
    void create(final List<MenuProduct> menuProducts) {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setMenuProducts(menuProducts)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포함할 제품 정보 목록에는 하나 이상의 제품 정보가 있어야 한다")
    @Test
    void createEmptyMenuProducts() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setMenuProducts(Collections.emptyList())
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포함할 제품 정보 목록의 길이와 포함할 제품의 식별자 목록으로 조회한 제품 목록의 길이는 같아야 한다")
    @ParameterizedTest
    @NullSource
    void create(final UUID productId) {
        final Product product = ProductBuilder.newInstance().setId(productId).build();
        final MenuProduct menuProduct = MenuProductBuilder.newInstance().setProduct(product).build();
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포함할 제품의 수량은 0 이상이어야 한다")
    @ParameterizedTest
    @ValueSource(longs = -1L)
    void create(final Long quantity) {
        final Product product = productRepository.save(ProductBuilder.newInstance().build());
        final MenuProduct menuProduct = MenuProductBuilder.newInstance()
                .setProduct(product)
                .setQuantity(quantity)
                .build();
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 포함할 제품의 (가격 * 수량)의 합 이하여야 한다")
    @Test
    void createIllegalMenuPrice() {
        final Product product = productRepository.save(ProductBuilder.newInstance()
                .setPrice(16_000L)
                .build());
        final MenuProduct menuProduct = MenuProductBuilder.newInstance()
                .setProduct(product)
                .build();
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setPrice(20_000L)
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 필수고, 비속어가 포함되지 않아야 한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"비속어", "욕설이 포함된 이름"})
    void create(final String name) {
        final Product product = productRepository.save(ProductBuilder.newInstance().build());
        final MenuProduct menuProduct = MenuProductBuilder.newInstance().setProduct(product).build();
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance()
                .setName(name)
                .setMenuProducts(menuProduct)
                .setMenuGroup(menuGroup)
                .build();

        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 메뉴의 식별자와 바꿀 가격으로 메뉴의 가격을 바꾼다")
    @Test
    void changePrice() {
        final Menu menu = menuRepository.save(MenuBuilder.newInstance().setPrice(16_000L).build());
        final Menu expected = MenuBuilder.newInstance().setPrice(15_000L).build();

        final Menu actual = menuService.changePrice(menu.getId(), expected);

        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @DisplayName("바꿀 가격은 필수고, 0 이상이어야 한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-16000")
    void changePrice(final BigDecimal price) {
        final Menu menu = menuRepository.save(MenuBuilder.newInstance().build());
        final Menu expected = MenuBuilder.newInstance().setPrice(price).build();

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 식별자로 특정 메뉴를 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void changePrice(UUID menuId) {
        final Menu expected = MenuBuilder.newInstance().setPrice(16_000L).build();

        assertThatThrownBy(() -> menuService.changePrice(menuId, expected))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("바꿀 가격이 특정 메뉴가 포함한 모든 제품의 (가격 * 수량)의 합 이하여야 한다")
    @Test
    void changePriceIllegalSum() {
        final Menu menu = menuRepository.save(MenuBuilder.newInstance().setPrice(16_000L).build());
        final Menu expected = MenuBuilder.newInstance().setPrice(17_000L).build();

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 메뉴의 식별자로 메뉴를 공개한다")
    @Test
    void display() {
        final Menu expected = menuRepository.save(MenuBuilder.newInstance()
                .setDisplayed(false)
                .build()
        );

        menuService.display(expected.getId());

        assertThat(menuRepository.findById(expected.getId())
                .get()
                .isDisplayed()
        ).isTrue();
    }

    @DisplayName("메뉴의 식별자로 특정 메뉴를 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void display(final UUID menuId) {
        assertThatThrownBy(() -> menuService.display(menuId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴의 가격이 메뉴가 포함한 모든 제품의 (가격 * 수량)의 합 이하여야 한다")
    @Test
    void displayIllegalSum() {
        final Menu expected = menuRepository.save(MenuBuilder.newInstance()
                .setMenuProducts(MenuProductBuilder.newInstance()
                        .setProduct(productRepository.save(ProductBuilder.newInstance().setPrice(16_000L).build()))
                        .setQuantity(2L)
                        .build())
                .setPrice(33_000L)
                .setDisplayed(false)
                .build());

        assertThatThrownBy(() -> menuService.display(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 메뉴의 식별자로 메뉴를 숨긴다")
    @Test
    void hide() {
        final Menu expected = menuRepository.save(MenuBuilder.newInstance().setDisplayed(true).build());

        menuService.hide(expected.getId());

        assertThat(menuRepository.findById(expected.getId())
                .get()
                .isDisplayed()
        ).isFalse();
    }

    @DisplayName("메뉴의 식별자로 특정 메뉴를 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void hide(UUID menuId) {
        assertThatThrownBy(() -> menuService.hide(menuId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 전체 목록을 조회한다")
    @Test
    void findAll() {
        final int expected = 2;

        IntStream.range(0, expected)
                .mapToObj(index -> MenuBuilder.newInstance().build())
                .forEach(menuRepository::save);

        assertThat(menuService.findAll()).hasSize(expected);
    }
}
