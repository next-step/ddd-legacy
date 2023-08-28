package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.MENU;
import static kitchenpos.fixture.MenuGroupFixture.MENU_GROUP;
import static kitchenpos.fixture.MenuProductFixture.MENU_PRODUCT;
import static kitchenpos.fixture.ProductFixture.PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("새로운 메뉴를 정상적으로 등록한다.")
    void createMenu() {
        // given
        Menu expected = MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU_GROUP()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(PRODUCT()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(menuRepository.save(any(Menu.class))).willReturn(expected);

        // when
        Menu actual = menuService.create(expected);

        // then
        assertThat(expected).isEqualTo(actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"-500", "-1"})
    @DisplayName("메뉴 가격은 0원 이상이어야 한다")
    void priceMoreThanOrEqualToZero(String price) {
        // given
        Menu expected = MENU();
        expected.setPrice(BigDecimal.valueOf(Integer.parseInt(price)));

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 상품이 있어야 한다.")
    void menuProductExists() {
        // given
        Menu expected = MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU_GROUP()));

        // when
        expected.setMenuProducts(null);

        //then
        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 상품의 개수는 0개 이상이어야 한다.")
    void productQuantityMoreThanOrEqualToZero() {
        // given
        Menu expected = MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));

        // when
        MenuProduct menuProduct = MENU_PRODUCT();
        menuProduct.setQuantity(-1);
        expected.setMenuProducts(List.of(menuProduct));

        // then
        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 총 가격은 상품과 가격을 곱한 값이어야 한다.")
    void sumEqualToPriceAndQuantityMultiplied() {
        // given
        Menu expected = MENU();
        Product product = PRODUCT();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when
        product.setPrice(new BigDecimal(3000));

        // then
        assertThatThrownBy(() -> menuService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"arse", "bastard", "null"})
    @DisplayName("메뉴의_이름은_부적절한_영어_이름이면_안된다")
    void menuNameCannotBeNullOrProfanity(String name) {
        // given
        Menu menuRequest = MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(PRODUCT()));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

        // when
        String value = Objects.isNull(name) ? null : name;
        menuRequest.setName(value);

        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("메뉴 하나의 가격을 수정한다.")
    void changeMenuPrice() {
        // given
        Menu expected = MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU()));

        // when
        expected.setPrice(new BigDecimal(10000));
        Menu actual = menuService.changePrice(expected.getId(), expected);

        // then
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    @DisplayName("수정되는 가격은 0 이상이어야 한다.")
    void newPriceGreaterOrEqualToZero() {
        // given
        Menu expected = MENU();

        // when
        expected.setPrice(new BigDecimal(-1));

        // then
        assertThatThrownBy(() -> menuService.changePrice(expected.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴에서 해당 상품의 가격과 개수를 곱한 총합이 가격보다 같거나 커야 한다.")
    void sumGreaterThanOrEqualToMenuPrice() {
        // given
        Menu expected = MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU()));

        // when
        expected.setPrice(new BigDecimal(20000));

        // then
        assertThatThrownBy(() -> menuService.changePrice(expected.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴가 화면에 노출되도록 한다.")
    void menuDisplayOn() {
        // given
        Menu expected = MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU()));

        // when
        expected.setDisplayed(false);
        Menu actual = menuService.display(expected.getId());

        // then
        assertThat(actual.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴에서 해당 상품의 가격과 개수를 곱한 총합이 가격보다 같거나 커야 한다.")
    void sumGreaterThanOrEqualToPrice() {
        // given
        Menu expected = MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(expected));

        // when
        expected.setPrice(new BigDecimal(20000));

        // then
        assertThatThrownBy(() -> menuService.display(expected.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("메뉴가 화면에 노출되지 않게 한다.")
    void menuDisplayOff() {
        // given
        Menu expected = MENU();
        expected.setDisplayed(true);
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(MENU()));

        // when
        Menu menu = menuService.hide(expected.getId());

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("모든 메뉴를 가져온다.")
    void findAllMenu() {
        // given
        given(menuRepository.findAll()).willReturn(List.of(new Menu(), new Menu()));

        // when
        List<Menu> actual = menuService.findAll();

        // then
        verify(menuRepository, times(1)).findAll();
        assertThat(actual).hasSize(2);
    }
}
