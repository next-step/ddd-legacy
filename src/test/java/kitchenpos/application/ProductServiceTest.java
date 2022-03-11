package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.fixture.MenuFixture.MenuBuilder;
import static kitchenpos.fixture.MenuProductFixture.MenuProductBuilder;
import static kitchenpos.fixture.ProductFixture.ProductBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    ProductService productService;
    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient 비속어_판별기;

    private static Stream<String> 잘못된_상품명() {
        return Stream.of(
                null,
                "XX치킨"
        );
    }

    private static Stream<BigDecimal> 잘못된_상품가격() {
        return Stream.of(
                null,
                new BigDecimal("-1")
        );
    }

    @DisplayName(value = "상품을 등록 할 수 있다")
    @Test
    void create_success() {
        //given
        Product 상품등록요청 = new ProductBuilder().name("후라이드치킨").price(BigDecimal.valueOf(17000L)).build();

        given(비속어_판별기.containsProfanity("후라이드치킨")).willReturn(false);

        //when
        Product 등록된상품 = productService.create(상품등록요청);

        //then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName(value = "상품은 반드시 상품가격을 가지며, 0원 이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_상품가격")
    void create_fail_invalid_price(final BigDecimal 상품가격) {
        //given
        Product 등록할상품 = new ProductBuilder().price(BigDecimal.valueOf(17000L)).build();

        //when, then
        assertThatThrownBy(() -> productService.create(등록할상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "상품은 반드시 상품명을 가지며, 비속어가 포함될 수 없다")
    @ParameterizedTest
    @MethodSource("잘못된_상품명")
    void create_fail_invalid_name(final String 상품명) {
        //given
        Product 등록할_상품 = new ProductBuilder().name(상품명).price(BigDecimal.valueOf(17000L)).build();

        //when
        lenient().when(비속어_판별기.containsProfanity(상품명)).thenReturn(true);

        //then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "상품 가격을 변경할 수 있다")
    @Test
    void changePrice_success() {
        //given
        Product 가격변경요청 = new ProductBuilder().price(BigDecimal.valueOf(17500L)).build();
        Product 가격변경될상품 = new ProductBuilder().price(BigDecimal.valueOf(17000L)).build();
        MenuProduct 상품구성 = new MenuProductBuilder().product(가격변경될상품).quantity(1L).build();
        Menu 가격변경될상품을포함한메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).menuProducts(new ArrayList<>(Arrays.asList(상품구성))).displayed(true).build();

        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(가격변경될상품));
        given(menuRepository.findAllByProductId(any(UUID.class))).willReturn(new ArrayList<>(Arrays.asList(가격변경될상품을포함한메뉴)));

        //when
        productService.changePrice(가격변경될상품.getId(), 가격변경요청);

        //then
        verify(productRepository, times(1)).findById(가격변경될상품.getId());
        verify(menuRepository, times(1)).findAllByProductId(가격변경될상품.getId());
        assertThat(가격변경될상품.getPrice()).isEqualTo(BigDecimal.valueOf(17500L));
        assertThat(가격변경될상품을포함한메뉴.isDisplayed()).isTrue();
    }

    @DisplayName(value = "변경하려는 상품의 가격은 0원 이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_상품가격")
    void changePrice_fail_invalid_price(final BigDecimal 변경할_상품가격) {
        //given
        Product 가격변경요청 = new ProductBuilder().price(변경할_상품가격).build();

        //when, then
        assertThatThrownBy(() -> productService.changePrice(가격변경요청.getId(), 가격변경요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "가격변경시 상품을 포함하고 있는 메뉴 가격이 각 상품 가격의 합보다 클 경우 메뉴 판매를 중단한다")
    @Test
    void changePrice_fail_menu_price_gt_product_price() {
        //given
        Product 가격변경_요청 = new ProductBuilder().price(BigDecimal.valueOf(16500)).build();
        Product 가격이변경될상품 = new ProductBuilder().price(BigDecimal.valueOf(17000)).build();;
        MenuProduct 상품구성 = new MenuProductBuilder().product(가격이변경될상품).quantity(1L).build();
        Menu 메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000)).displayed(true).menuProducts(new ArrayList<>(Arrays.asList(상품구성))).build();

        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(가격이변경될상품));
        given(menuRepository.findAllByProductId(any(UUID.class))).willReturn(new ArrayList<>(Arrays.asList(메뉴)));

        //when
        productService.changePrice(UUID.randomUUID(), 가격변경_요청);

        //then
        verify(productRepository, times(1)).findById(any(UUID.class));
        verify(menuRepository, times(1)).findAllByProductId(any(UUID.class));
        assertThat(가격이변경될상품.getPrice()).isEqualTo(BigDecimal.valueOf(16500));
        assertThat(메뉴.isDisplayed()).isFalse();
    }

    @DisplayName(value = "전체 상품리스트를 조회할 수 있다")
    @Test
    void findAll_success() {
        //given, when
        productService.findAll();

        // then
        verify(productRepository, times(1)).findAll();
    }
}
