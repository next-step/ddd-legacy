package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks ProductService productService;
    @Mock ProductRepository productRepository;
    @Mock MenuRepository menuRepository;
    @Mock        PurgomalumClient 비속어_판별기;

    @BeforeEach
    void setUp() {

    }

    @DisplayName(value = "상품을 등록 할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"후라이드치킨"})
    void create_success(final String 상품명) {
        //given
        Product 등록할_상품 = mock(Product.class);
        given(등록할_상품.getPrice()).willReturn(new BigDecimal("1000"));
        given(등록할_상품.getName()).willReturn(상품명);
        given(비속어_판별기.containsProfanity(상품명)).willReturn(false);

        //when
        productService.create(등록할_상품);

        //then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName(value = "상품은 반드시 상품가격을 가지며, 0원 이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_상품가격")
    void create_fail_invalid_price(final BigDecimal 상품가격) {
        //given
        Product 등록할_상품 = mock(Product.class);
        given(등록할_상품.getPrice()).willReturn(상품가격);

        //when, then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "상품은 반드시 상품명을 가지며, 비속어가 포함될 수 없다")
    @ParameterizedTest
    @MethodSource("잘못된_상품명")
    void create_fail_invalid_name(final String 상품명) {
        //given
        Product 등록할_상품 = mock(Product.class);
        given(등록할_상품.getPrice()).willReturn(new BigDecimal("1000"));
        given(등록할_상품.getName()).willReturn(상품명);

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
        BigDecimal  변경할_상품가격 = new BigDecimal("17500");
        BigDecimal 메뉴가격 = new BigDecimal("17000");

        Product     가격변경_요청 = mock(Product.class);
        given(가격변경_요청.getPrice()).willReturn(변경할_상품가격);

        Product 가격이_변경될_상품 = mock(Product.class);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(가격이_변경될_상품));
        given(가격이_변경될_상품.getPrice()).willReturn(변경할_상품가격);

        Menu 메뉴 = mock(Menu.class);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByProductId(any(UUID.class))).willReturn(new ArrayList<>(Arrays.asList(메뉴)));

        MenuProduct 상품구성 = mock(MenuProduct.class);
        given(상품구성.getProduct()).willReturn(가격이_변경될_상품);
        given(상품구성.getQuantity()).willReturn(1L);
        given(메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(상품구성)));

        //when
        productService.changePrice(UUID.randomUUID(), 가격변경_요청);

        //then
        verify(productRepository, times(1)).findById(any(UUID.class));
        verify(가격이_변경될_상품, times(1)).setPrice(변경할_상품가격);
        verify(menuRepository, times(1)).findAllByProductId(any(UUID.class));
        verify(메뉴,times(0)).setDisplayed(false);
    }

    @DisplayName(value = "변경하려는 상품의 가격은 0원 이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_상품가격")
    void changePrice_fail_invalid_price(final BigDecimal 변경할_상품가격) {
        //given
        Product     가격변경_요청   = mock(Product.class);
        given(가격변경_요청.getPrice()).willReturn(변경할_상품가격);

        //when, then
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), 가격변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "가격변경시 상품을 포함하고 있는 메뉴 가격이 각 상품 가격의 합보다 클 경우 메뉴 판매를 중단한다")
    @Test
    void changePrice_fail_menu_price_gt_product_price() {
        //given
        BigDecimal  변경할_상품가격 = new BigDecimal("16500");
        BigDecimal 메뉴가격 = new BigDecimal("17000");

        Product     가격변경_요청 = mock(Product.class);
        given(가격변경_요청.getPrice()).willReturn(변경할_상품가격);

        Product 가격이_변경될_상품 = mock(Product.class);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(가격이_변경될_상품));
        given(가격이_변경될_상품.getPrice()).willReturn(변경할_상품가격);

        Menu 메뉴 = mock(Menu.class);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByProductId(any(UUID.class))).willReturn(new ArrayList<>(Arrays.asList(메뉴)));

        MenuProduct 상품구성 = mock(MenuProduct.class);
        given(상품구성.getProduct()).willReturn(가격이_변경될_상품);
        given(상품구성.getQuantity()).willReturn(1L);
        given(메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(상품구성)));

        //when
        productService.changePrice(UUID.randomUUID(), 가격변경_요청);

        //then
        verify(productRepository, times(1)).findById(any(UUID.class));
        verify(가격이_변경될_상품, times(1)).setPrice(변경할_상품가격);
        verify(menuRepository, times(1)).findAllByProductId(any(UUID.class));
        verify(메뉴,times(1)).setDisplayed(false);
    }


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


}