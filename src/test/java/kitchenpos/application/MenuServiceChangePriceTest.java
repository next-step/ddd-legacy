package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceChangePriceTest extends AbstractApplicationServiceTest {

    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(1_000L);
    private static final String TEST_MENU_NAME = "dummyMenuName";
    private static final String TEST_PRODUCT_NAME = "dummyProductName";
    private static final String TEST_MENU_GROUP_NAME = "dummyMenuGroup";

    @Mock
    private PurgomalumClient mockClient;

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

    @DisplayName("메뉴 가격을 변경할 수 있다.")
    @Test
    void changePrice_success() {
        // given
        final Product product = productRepository.save(
            createProductRequest(TEST_PRODUCT_NAME, TEST_PRICE));
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Menu menu = menuRepository.save(create(product, menuGroup));
        final Menu request = create(BigDecimal.valueOf(500L));

        // when
        final Menu actual = service.changePrice(menu.getId(), request);

        // then
        assertThat(actual.getPrice())
            .isEqualTo(request.getPrice());
    }

    private Menu create(final BigDecimal price) {
        return createMenuRequest(TEST_MENU_NAME, price, ImmutableList.of(),
            createMenuGroupRequest(TEST_MENU_GROUP_NAME), false);
    }

    @DisplayName("메뉴 가격을 변경할 때 가격이 없거나 음수면 예외를 발생시킨다.")
    @Test
    void changePrice_invalid_price_1() {
        // given
        final UUID menuId = UUID.randomUUID();
        final Menu negativePrice = create(BigDecimal.valueOf(-1));
        final Menu nullPrice = create(null);

        // when & then
        assertThatThrownBy(() -> service.changePrice(menuId, negativePrice))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> service.changePrice(menuId, nullPrice))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 변경할 때 가격이 menuProduct들의 수량과 product의 가격을 곱한 값의 총 합보다 크면 예외를 발생시킨다.")
    @Test
    void changePrice_invalid_price_2() {
        // given
        final Product product = productRepository.save(createProductRequest("product", TEST_PRICE));
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Menu menu = menuRepository.save(create(product, menuGroup));
        final Menu request = create(BigDecimal.valueOf(10_000L));

        // when & then
        assertThatThrownBy(() -> service.changePrice(menu.getId(), request));

    }

    private Menu create(final Product product, final MenuGroup menuGroup) {
        return createMenuRequest(TEST_MENU_NAME, TEST_PRICE,
            ImmutableList.of(createMenuProductRequest(product, 1)), menuGroup, false);
    }
}
