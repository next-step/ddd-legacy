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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    private static Stream<BigDecimal> providePriceForNullAndNegative() {
        return Stream.of(
                null,
                BigDecimal.valueOf(-1000L)
        );
    }

    @DisplayName("상품등록 - 상품의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    void create01(BigDecimal 등록할_상품_가격) {
        //given
        Product 등록할_상품 = mock(Product.class);
        when(등록할_상품.getPrice()).thenReturn(등록할_상품_가격);
        //when & then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품은 반드시 이름을 가진다.")
    @Test
    void create02() {
        //given
        Product 등록할_상품 = mock(Product.class);
        BigDecimal 등록할_상품_가격 = BigDecimal.valueOf(1000L);
        when(등록할_상품.getPrice()).thenReturn(등록할_상품_가격);
        //when & then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품의 이름은 비속어를 포함할 수 없다.")
    @Test
    void create03() {
        //given

        Product 등록할_상품 = mock(Product.class);
        BigDecimal 등록할_상품_가격 = BigDecimal.valueOf(1000L);
        String 등록할_상품_이름 = "X나 맛없는 미트파이";
        when(등록할_상품.getPrice()).thenReturn(등록할_상품_가격);
        when(등록할_상품.getName()).thenReturn(등록할_상품_이름);
        when(purgomalumClient.containsProfanity(등록할_상품_이름))
                .thenReturn(Boolean.TRUE);
        //when & then
        assertThatThrownBy(() -> productService.create(등록할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품을 등록할 수 있다.")
    @Test
    void create04() {
        //given
        Product 등록할_상품 = mock(Product.class);
        BigDecimal 등록할_상품_가격 = BigDecimal.valueOf(1000L);
        String 등록할_상품_이름 = "맛있는 미트파이";
        when(등록할_상품.getPrice()).thenReturn(등록할_상품_가격);
        when(등록할_상품.getName()).thenReturn(등록할_상품_이름);
        when(purgomalumClient.containsProfanity(등록할_상품_이름))
                .thenReturn(Boolean.FALSE);
        //when
        productService.create(등록할_상품);

        // & then
        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("상품 가격 수정 - 상품의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    void changePrice01(BigDecimal 변경할_상품_가격) {
        //given
        Product 변경할_상품 = mock(Product.class);
        UUID 변경할_상품_아이디 = UUID.randomUUID();
        when(변경할_상품.getPrice()).thenReturn(변경할_상품_가격);
        //when & then
        assertThatThrownBy(() -> productService.changePrice(변경할_상품_아이디, 변경할_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격 수정 - 가격을 변경하는 상품을 포함하는 메뉴의 가격보다 메뉴에 포함한 상품의 가격이 커지는 경우 메뉴를 진열하지 않는다.")
    @Test
    void changePrice02() {
        //given
        BigDecimal 변경할_상품_가격 = BigDecimal.valueOf(2700L);
        BigDecimal 기존_상품_가격 = BigDecimal.valueOf(3000L);
        BigDecimal 계속_공개될_메뉴_가격 = BigDecimal.valueOf(2500L);
        BigDecimal 비공개될_메뉴_가격 = BigDecimal.valueOf(2800L);

        Product 변경할_상품 = mock(Product.class);
        given(변경할_상품.getPrice()).willReturn(변경할_상품_가격);
        변경할_상품.setPrice(변경할_상품_가격);

        Product 저장된_상품 = spy(Product.class);
        저장된_상품.setPrice(기존_상품_가격);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(저장된_상품));

        MenuProduct 저장된_메뉴_상품 = mock(MenuProduct.class);
        given(저장된_메뉴_상품.getProduct()).willReturn(저장된_상품);
        given(저장된_메뉴_상품.getQuantity()).willReturn(1L);

        Menu 계속_공개될_메뉴 = mock(Menu.class);
        given(계속_공개될_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(저장된_메뉴_상품)));
        given(계속_공개될_메뉴.getPrice()).willReturn(계속_공개될_메뉴_가격);

        Menu 비공개될_메뉴 = mock(Menu.class);
        given(비공개될_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(저장된_메뉴_상품)));
        given(비공개될_메뉴.getPrice()).willReturn(비공개될_메뉴_가격);

        given(menuRepository.findAllByProductId(any(UUID.class)))
                .willReturn(new ArrayList<>(Arrays.asList(계속_공개될_메뉴, 비공개될_메뉴)));

        //when
        productService.changePrice(UUID.randomUUID(), 변경할_상품);

        //then

        verify(계속_공개될_메뉴, times(0)).setDisplayed(anyBoolean());
        verify(비공개될_메뉴).setDisplayed(false);
    }

    @DisplayName("상품 가격 수정 - 상품의 가격을 수정할 수 있다.")
    @Test
    void changePrice03() {
        //given
        BigDecimal 변경할_상품_가격 = BigDecimal.valueOf(2700L);
        BigDecimal 기존_상품_가격 = BigDecimal.valueOf(3000L);
        BigDecimal 계속_공개될_메뉴_가격 = BigDecimal.valueOf(2500L);

        Product 변경할_상품 = new Product();
        변경할_상품.setPrice(변경할_상품_가격);
        Product 저장된_상품 = spy(Product.class);
        저장된_상품.setPrice(기존_상품_가격);
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(저장된_상품));

        MenuProduct 저장된_메뉴_상품 = mock(MenuProduct.class);
        given(저장된_메뉴_상품.getProduct()).willReturn(저장된_상품);
        given(저장된_메뉴_상품.getQuantity()).willReturn(1L);

        Menu 계속_공개될_메뉴 = mock(Menu.class);
        given(계속_공개될_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(저장된_메뉴_상품)));
        given(계속_공개될_메뉴.getPrice()).willReturn(계속_공개될_메뉴_가격);

        given(menuRepository.findAllByProductId(any(UUID.class)))
                .willReturn(new ArrayList<>(Arrays.asList(계속_공개될_메뉴)));
        //when
        productService.changePrice(UUID.randomUUID(), 변경할_상품);

        //then
        verify(저장된_상품).setPrice(변경할_상품_가격);
        verify(계속_공개될_메뉴, times(0)).setDisplayed(anyBoolean());
    }

    @DisplayName("상품 조회 - 등록된 모든 상품을 조회할 수 있다.")
    @Test
    void findAll() {
        // given & when
        productService.findAll();
        //then
        verify(productRepository).findAll();
    }
}
