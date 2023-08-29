package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceForCreateTest extends AbstractApplicationServiceTest {

    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(1_000L);
    private static final String TEST_MENU_NAME = "dummyMenuName";
    private static final String TEST_MENU_GROUP_NAME = "dummyMenuGroup";
    private static final String TEST_PRODUCT_NAME = "dummyProductName";

    @Mock
    protected PurgomalumClient mockClient;

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    private MenuService service;

    @BeforeEach
    void setUp() {
        menuRepository = new MenuFakeRepository();
        menuGroupRepository = new MenuGroupFakeRepository();
        productRepository = new ProductFakeRepository();

        service = new MenuService(menuRepository, menuGroupRepository,
            productRepository, mockClient);
    }

    @DisplayName("메뉴를 생성하여 반환한다.")
    @Test
    void create_success() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Product product = productRepository.save(createProductRequest("product", TEST_PRICE));
        final Menu request = create(product, 1, menuGroup);

        // when
        final Menu actual = service.create(request);

        // then
        assertThat(actual.getId()).isNotNull();
    }

    @DisplayName("메뉴 가격이 없거나 음수면 예외를 발생시킨다.")
    @Test
    void create_invalid_price_1() {
        // given
        final Menu nullPrice = create(null);
        final Menu negativePrice = create(BigDecimal.valueOf(-1L));

        // when & then
        assertThatThrownBy(() -> service.create(nullPrice))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> service.create(negativePrice))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private Menu create(final BigDecimal price) {
        return createMenuRequest(TEST_MENU_NAME, price, List.of(),
            createMenuGroupRequest(TEST_MENU_GROUP_NAME), false);
    }

    @DisplayName("메뉴의 가격이 menuProduct들의 수량과 product의 가격을 곱한 값의 총 합보다 크면 예외를 발생시킨다.")
    @Test
    void create_invalid_price_2() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Product product = productRepository.save(
            createProductRequest(TEST_PRODUCT_NAME, TEST_PRICE));
        final Menu request = create(product, 1, menuGroup);

        // when
        final Menu actual = service.create(request);

        // then
        assertThat(actual.getId()).isNotNull();
    }

    @DisplayName("메뉴 그룹이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void create_invalid_menuGroup() {
        // given
        final Menu request = createMenuRequest(TEST_MENU_NAME, TEST_PRICE,
            List.of(), createMenuGroupRequest(TEST_MENU_GROUP_NAME), false);

        // then & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴에 한개 이상의 menuProduct가 없으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_invalid_menuProduct(final List<MenuProduct> value) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Menu menu = createMenuRequest(TEST_MENU_NAME, TEST_PRICE, value, menuGroup, false);

        // when & then
        assertThatThrownBy(() -> service.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("menuProduct의 수량이 음수면 예외를 발생시킨다.")
    @Test
    void create_invalid_menuProduct_1() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Product product = productRepository.save(
            createProductRequest(TEST_PRODUCT_NAME, TEST_PRICE));
        final Menu request = create(product, -1, menuGroup);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("menuProduct의 상품이 등록된 상품이 아니면 예외를 발생시킨다.")
    @Test
    void create_invalid_menuProduct_2() {
        // given
        final Product product = createProductRequest("product", TEST_PRICE);
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Menu request = create(product, menuGroup);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 이름이 없으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullSource
    void create_invalid_name_null(final String value) {
        // given
        final Product product = productRepository.save(
            createProductRequest(TEST_PRODUCT_NAME, TEST_PRICE));
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Menu request = create(value, product, menuGroup);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름에 비속어가 포함되어있으면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = "비속어")
    void create_invalid_name_profanity(final String value) {
        // given
        final Product product = productRepository.save(
            createProductRequest(TEST_PRODUCT_NAME, TEST_PRICE));
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        doReturn(true)
            .when(mockClient)
            .containsProfanity(value);

        final Menu request = create(value, product, menuGroup);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }


    private Menu create(final String name, final Product product, final MenuGroup menuGroup) {
        return createMenuRequest(name, TEST_PRICE,
            ImmutableList.of(createMenuProductRequest(product, 1)), menuGroup, false);
    }

    private Menu create(final Product product, final MenuGroup menuGroup) {
        return create(product, 1, menuGroup);
    }

    private Menu create(final Product product, final long quantity, final MenuGroup menuGroup) {
        return createMenuRequest(TEST_MENU_NAME, TEST_PRICE,
            ImmutableList.of(createMenuProductRequest(product, quantity)), menuGroup, false);
    }
}
