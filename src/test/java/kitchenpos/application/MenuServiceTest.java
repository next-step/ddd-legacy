package kitchenpos.application;

import static java.math.BigDecimal.valueOf;
import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuProductRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import static kitchenpos.fixtures.MenuFixtures.createMenu;
import static kitchenpos.fixtures.MenuFixtures.createMenuGroup;
import static kitchenpos.fixtures.MenuFixtures.createMenuProduct;
import static kitchenpos.fixtures.MenuFixtures.createMenuWithoutMenuGroupId;
import static kitchenpos.fixtures.ProductFixtures.createProduct;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
class MenuServiceTest extends IntegrationTestSupport {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private MenuProductRepository menuProductRepository;

    /**
     * 매 테스트 실행 후 DB를 정리하여 일관된 테스트 환경을 유지한다.
     */
    @AfterEach
    void tearDown() {
//        menuRepository.deleteAllMenuProducts(); // 방법1
        menuProductRepository.deleteAllInBatch(); // 방법2
        menuRepository.deleteAllInBatch();
        menuGroupRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void createMenu_Success() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);

        // when
        Menu actual = menuService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getMenuGroup()).isEqualTo(expected.getMenuGroup()),
            () -> assertThat(actual.getMenuProducts()).hasSize(expected.getMenuProducts().size())
        );
    }

    @DisplayName("메뉴에 가격이 0원 미만이면 등록할 수 없다.")
    @Test
    void createMenu_WhenPriceIsNegative_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(-10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(-10000), true, menuGroup, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가격이 존재하거나 0원 이상이어야 합니다.");
    }

    @DisplayName("특정 메뉴 그룹이 존재하지 않으면 등록할 수 없다.")
    @Test
    void createMenu_WhenMenuGroupIsMissing_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        Menu expected = createMenuWithoutMenuGroupId("김치찌개 세트", valueOf(10000), true, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("메뉴는 특정 메뉴 그룹에 속해야 한다.");
    }

    @DisplayName("메뉴에 포함된 상품이 없으면 등록할 수 없다.")
    @Test
    void createMenu_WhenMenuProductsAreEmpty_ShouldThrowException() {
        // given
        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        List<MenuProduct> emptyMenuProducts = List.of();

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, emptyMenuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴에 포함된 상품이 존재해야 합니다.");
    }

    @DisplayName("메뉴에 등록된 상품의 수량이 0개 미만이면 등록할 수 없다.")
    @Test
    void createMenu_WhenQuantityIsNegative_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), -1L);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름이 없으면(null, 빈 값) 등록할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void createMenu_WhenMenuNameIsNull_ThrowsException(String invalidMenuName) {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        // when
        Menu expected = createMenu(invalidMenuName, valueOf(10000), true, menuGroup, menuProducts);

        // then
        Assertions.assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바른 메뉴 이름을 입력해야 합니다.");
    }

    @DisplayName("메뉴 이름에 부적절한 단어가 포함되면 등록할 수 없다.")
    @Test
    void createMenu_WhenContainsProfanity_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        String profanityWord = "비속어";
        String invalidMenuName = "김치찌개 세트" + profanityWord;

        // when
        when(purgomalumClient.containsProfanity(invalidMenuName)).thenReturn(true);
        Menu expected = createMenu(invalidMenuName, valueOf(10000), true, menuGroup, menuProducts);

        // then
        Assertions.assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바른 메뉴 이름을 입력해야 합니다.");
    }

    @DisplayName("메뉴에 있는 가격을 상품들의 총 가격 이하로 변경할 수 있다.")
    @Test
    void changeMenuPrice_Success() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(9999));
        Menu changePriceMenu = menuService.changePrice(actual.getId(), actual);

        // then
        assertThat(changePriceMenu.getPrice()).isEqualTo(valueOf(9999));

    }

    @DisplayName("메뉴 가격이 0원 미만이면 변경할 수 없다.")
    @Test
    void changeMenuPrice_WhenPriceIsNegative_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(-1));

        // then
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 변경시 가격이 0원 이상이어야 합니다.");
    }

    @DisplayName("메뉴 가격이 상품 가격 합보다 크면 변경할 수 없다.")
    @Test
    void changeMenuPrice_WhenPriceExceedsSum_ShouldThrowException() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(10001));

        // then
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격은 포함된 상품 가격 합보다 클 수 없습니다.");
    }

    @DisplayName("메뉴를 표시 상태로 변경할 수 있다.")
    @Test
    void displayMenu_Success() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        Menu displayMenu = menuService.display(actual.getId());

        // then
        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @DisplayName("존재하지 않는 메뉴를 표시할 수 없다.")
    @Test
    void displayMenu_WhenMenuDoesNotExist_ShouldThrowException() {
        // given
        UUID NonExistedMenuId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> menuService.display(NonExistedMenuId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("해당 ID의 메뉴가 존재하지 않습니다.");
    }

    @DisplayName("메뉴 가격이 상품 가격 합보다 높으면 메뉴를 표시할 수 없다.")
    @Test
    void displayMenu_WhenPriceExceedsSum_ShouldThrowException() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        // 상품 가격보다 높은 메뉴 가격 설정
        Menu expected = createMenu("김치찌개 세트", valueOf(10000), false, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(10001));
        menuRepository.save(actual);

        // then
        assertThatThrownBy(() -> menuService.display(actual.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("메뉴 가격이 포함된 상품 가격보다 높아 표시할 수 없습니다.");
    }

    @DisplayName("등록된 메뉴를 숨길 수 있다.")
    @Test
    void hideMenu_Success() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);

        // when
        Menu actual = menuService.create(expected);
        Menu hidMenu = menuService.hide(actual.getId());

        // then
        assertThat(hidMenu.isDisplayed()).isFalse();
    }

    @DisplayName("등록된 메뉴를 모두 조회할 수 있다.")
    @Test
    void findAllMenu_Success() {
        // given
        Product product = createProduct("김치찌개", valueOf(10000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("김치찌개 세트", valueOf(10000), true, menuGroup, menuProducts);
        menuService.create(expected);

        // when
        List<Menu> findAllMenu = menuService.findAll();

        // then
        assertThat(findAllMenu).isNotEmpty();
        assertThat(findAllMenu.size()).isEqualTo(1);
    }
}
