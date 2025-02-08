package kitchenpos.application;

import static java.math.BigDecimal.valueOf;
import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixtures.MenuFixtures;
import static kitchenpos.fixtures.MenuFixtures.createMenuGroup;
import static kitchenpos.fixtures.MenuFixtures.createMenuProduct;
import static kitchenpos.fixtures.MenuFixtures.createProduct;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

//    @AfterEach
//    void tearDown() {
//        menuRepository.deleteAllInBatch();
//        menuGroupRepository.deleteAllInBatch();
//        productRepository.deleteAllInBatch();
//    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void createMenu_Success() {
        // given
        Product product = createProduct("김치찌개", valueOf(8000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = MenuFixtures.createMenu("김치찌개 세트", valueOf(8000), true, menuGroup, menuProducts);

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
        Product product = createProduct("김치찌개", valueOf(-8000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = MenuFixtures.createMenu("김치찌개 세트", valueOf(-8000), true, menuGroup, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가격이 존재하거나 0원 이상이어야 합니다.");
    }

    @DisplayName("특정 메뉴 그룹이 존재하지 않으면 등록할 수 없다.")
    @Test
    void createMenu_WhenMenuGroupIsMissing_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(-8000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        Menu expected = MenuFixtures.createMenuWithoutMenuGroupId("김치찌개 세트", valueOf(8000), true, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("메뉴는 특정 메뉴 그룹에 속해야 한다.");
    }

    @DisplayName("메뉴에 등록된 상품의 수량이 0개 미만이면 등록할 수 없다.")
    @Test
    void createMenu_WhenQuantityIsNegative_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(8000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), -1L);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = MenuFixtures.createMenu("김치찌개 세트", valueOf(8000), true, menuGroup, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름이 없으면 등록할 수 없다.")
    @Test
    void createMenu_WhenMenuNameIsNull_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(8000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        // when
        Menu expected = MenuFixtures.createMenu(null, valueOf(8000), true, menuGroup, menuProducts);

        // then
        Assertions.assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바른 메뉴 이름을 입력해야 합니다.");
     }

    @DisplayName("메뉴 이름에 부적절한 단어가 포함되면 등록할 수 없다.")
    @Test
    void createMenu_WhenContainsProfanity_ThrowsException() {
        // given
        Product product = createProduct("김치찌개", valueOf(8000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        String profanityWord = "비속어";
        String invalidMenuName = "김치찌개 세트" + profanityWord;

        // when
        when(purgomalumClient.containsProfanity(invalidMenuName)).thenReturn(true);
        Menu expected = MenuFixtures.createMenu(invalidMenuName, valueOf(8000), true, menuGroup, menuProducts);

        // then
        Assertions.assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바른 메뉴 이름을 입력해야 합니다.");
    }
}
