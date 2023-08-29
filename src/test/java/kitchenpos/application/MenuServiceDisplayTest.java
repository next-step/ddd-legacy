package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
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
class MenuServiceDisplayTest extends AbstractApplicationServiceTest {

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

    @DisplayName("메뉴를 노출처리 할 수 있다.")
    @Test
    void display_success() {
        // given
        final Menu menu = menuRepository.save(
            createMenuRequest(TEST_MENU_NAME, BigDecimal.valueOf(1_000L), List.of(),
                createMenuGroupRequest(TEST_MENU_GROUP_NAME), false));

        // when
        final Menu actual = service.display(menu.getId());

        // then
        assertThat(actual.isDisplayed()).isTrue();
    }


    @DisplayName("메뉴 노출처리시 없는 메뉴면 예외를 발생시킨다.")
    @Test
    void display_invalid_param() {
        // given

        // when & then
        assertThatThrownBy(() -> service.display(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 노출처리시 메뉴의 가격이 menuProduct들의 수량과 product의 가격을 곱한 값의 총 합보다 크면 예외를 발생시킨다.")
    @Test
    void display_invalid_pricate() {
        // given
        final Product product = productRepository.save(
            createProductRequest(TEST_PRODUCT_NAME, BigDecimal.valueOf(1_000L)));
        final MenuGroup menuGroup = menuGroupRepository.save(
            createMenuGroupRequest(TEST_MENU_GROUP_NAME));
        final Menu menu = menuRepository.save(create(product, menuGroup));

        // when
        final Menu actual = service.display(menu.getId());

        // then
        assertThat(actual.isDisplayed()).isTrue();
    }

    private Menu create(final Product product, final MenuGroup menuGroup) {
        return createMenuRequest(TEST_MENU_NAME, TEST_PRICE,
            ImmutableList.of(createMenuProductRequest(product, 1)), menuGroup, false);
    }

    @DisplayName("메뉴를 숨김처리 할 수 있다.")
    @Test
    void hide_success() {
        // given
        final Menu menu = menuRepository.save(
            createMenuRequest(TEST_MENU_NAME, BigDecimal.valueOf(1_000L), List.of(),
                createMenuGroupRequest(TEST_MENU_GROUP_NAME), true));

        // when
        final Menu actual = service.hide(menu.getId());

        // then
        assertThat(actual.isDisplayed()).isFalse();
    }


    @DisplayName("메뉴 숨김처리시 없는 메뉴면 예외를 발생시킨다.")
    @Test
    void hide_invalid_param() {
        // given

        // when & then
        assertThatThrownBy(() -> service.display(UUID.randomUUID()))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }
}
