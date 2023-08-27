package kitchenpos.application;

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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @InjectMocks
    private MenuService menuService;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @Test
    @DisplayName("새로운 메뉴를 등록한다.")
    void createTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_PRODUCT()));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(menuRepository.save(any(Menu.class))).willReturn(TEST_MENU());

        // when
        menuService.create(menuRequest);

        // then
        verify(menuGroupRepository, times(1)).findById(any(UUID.class));
        verify(productRepository, times(1)).findAllByIdIn(any());
        verify(productRepository, times(1)).findById(any(UUID.class));
        verify(purgomalumClient, times(1)).containsProfanity(anyString());
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @ParameterizedTest
    @CsvSource(value = {"-50030", "-1", "null"})
    @DisplayName("메뉴의_가격은_0원_이상이여야_한다")
    void priceTest(String price) {
        // given
        Menu menuRequest = TEST_MENU();

        // when
        BigDecimal value = price.equals("null") ? null : new BigDecimal(price);
        menuRequest.setPrice(value);

        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴_생성시_상품을_등록해야한다")
    void productRequiredTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

        // when
        menuRequest.setMenuProducts(null);

        //then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴_생성시_상품들은_등록된_상품이어야_한다")
    void productShouldExist() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

        // when
        List<Product> emptyList = Collections.emptyList();
        given(productRepository.findAllByIdIn(anyList())).willReturn(emptyList);

        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의_상품들은_수량은_0개_이상이어야_한다")
    void quantityTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));

        // when
        MenuProduct menuProduct = TEST_MENU_PRODUCT();
        menuProduct.setQuantity(-1);
        menuRequest.setMenuProducts(List.of(menuProduct));

        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 포함된 상품들의 각 금액(상품 가격 X 수량)의 합보다 높을 수 없다.")
    void priceSumTest() {
        // given
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));

        // when
        Menu menuRequest = TEST_MENU();
        menuRequest.setPrice(new BigDecimal(50_000));
        Product product = TEST_PRODUCT();
        product.setPrice(new BigDecimal(10));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의_이름은_빈값이면_안된다")
    void menuNameNotNull() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_PRODUCT()));

        // when
        menuRequest.setName(null);

        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"fuck", "shit"})
    @DisplayName("메뉴의_이름은_부적절한_영어_이름이면_안된다")
    void profanityTest(String name) {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_PRODUCT()));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

        // when
        menuRequest.setName(name);
        // then
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("기존_메뉴의_가격을_수정한다")
    void menuPriceChangeTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU()));

        // when
        Menu changePrice = TEST_MENU();
        changePrice.setPrice(new BigDecimal(777));
        Menu menu = menuService.changePrice(menuRequest.getId(), changePrice);

        // then
        verify(menuRepository, times(1)).findById(any(UUID.class));
        assertThat(menu.getPrice()).isEqualTo(changePrice.getPrice());
    }

    @Test
    @DisplayName("변경할_가격은_0원_이상이어야_한다")
    void changePriceTest() {
        // given
        Menu menuRequest = TEST_MENU();

        // when
        Menu changePrice = TEST_MENU();
        changePrice.setPrice(new BigDecimal(-1));

        // then
        assertThatThrownBy(() -> menuService.changePrice(menuRequest.getId(), changePrice))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("변경할 메뉴의 가격은 포함된 상품들의 각 금액(상품 가격 X 수량)의 합보다 높을 수 없다")
    void priceChangeTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU()));

        // when
        Menu changePrice = TEST_MENU();
        changePrice.setPrice(new BigDecimal(999_999_999));

        // then
        assertThatThrownBy(() -> menuService.changePrice(menuRequest.getId(), changePrice))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("기존_메뉴가_사용자에게_보이도록_활성화한다")
    void menuDisplayTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU()));

        // when
        menuRequest.setDisplayed(false);
        Menu menu = menuService.display(menuRequest.getId());

        // then
        verify(menuRepository, times(1)).findById(any(UUID.class));
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("활성화 시킬 메뉴의 가격은 포함된 상품들의 각 금액(상품 가격 X 수량)의 합보다 높을 수 없다")
    void displayChangeTest() {
        // given
        Menu request = TEST_MENU();

        // when
        request.setPrice(new BigDecimal(59000));
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(request));

        // then
        assertThatThrownBy(() -> menuService.display(request.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("기존_메뉴가_사용자에게_보이지_않도록_비활성화한다")
    void hideTest() {
        // given
        Menu menuRequest = TEST_MENU();
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU()));

        // when
        menuRequest.setDisplayed(true);
        Menu menu = menuService.hide(menuRequest.getId());

        // then
        verify(menuRepository, times(1)).findById(any(UUID.class));
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("모든_메뉴_정보를_가져온다")
    void findAllTest() {
        // given
        given(menuRepository.findAll()).willReturn(List.of(new Menu(), new Menu(), new Menu()));

        // when
        List<Menu> menus = menuService.findAll();

        // then
        verify(menuRepository, times(1)).findAll();
        assertThat(menus).isNotNull();
        assertThat(menus).hasSize(3);
    }
}
