package kitchenpos.product.application;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.menu.fixture.MenuFixture.메뉴;
import static kitchenpos.menu.fixture.MenuFixture.메뉴_상품;
import static kitchenpos.product.fixture.ProductionFixture.상품;
import static kitchenpos.product.fixture.ProductionFixture.상품_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@DisplayName("Product 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록한다.")
    @ParameterizedTest
    @ValueSource(strings = {"후라이드","양념"})
    public void create(String name) {
        // given
        int 가격 = 17000;
        Product 상품_요청 = 상품_요청(name, 가격);
        Product 상품 = 상품(name, 가격);
        given(purgomalumClient.containsProfanity(상품.getName())).willReturn(false);
        given(productRepository.save(any())).willReturn(상품);

        // when
        Product product = productService.create(상품_요청);

        // then
        assertAll(
                () -> assertThat(product.getId()).isInstanceOf(UUID.class),
                () -> assertThat(product.getName()).isEqualTo(name),
                () -> assertThat(product.getPrice()).isEqualTo(new BigDecimal(가격))
        );
    }

    @DisplayName("상품 가격이 음수거나 null인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-100})
    void createWithNegativePrice(int price) {
        // given
        Product 강정치킨 = 상품_요청("강정치킨", price);

        // when, then
        assertThatThrownBy(() -> productService.create(강정치킨))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에 욕설을 포함된 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck","shit"})
    void createWithoutBadWords(String name) {
        // given
        Product 욕설을_포함한_상품 = 상품_요청(name, 17000);
        when(purgomalumClient.containsProfanity(욕설을_포함한_상품.getName())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> productService.create(욕설을_포함한_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품가격을 변경한다.")
    @Test
    public void changePrice() {
        // given
        int 변경될_가격 = 17000;
        Product 변경될_가격_상품_요청 = 상품_요청("후라이드", 변경될_가격);

        Product 후라이드 = 상품("후라이드", 15000);
        given(productRepository.findById(any())).willReturn(Optional.of(후라이드));

        Menu menu = 메뉴("후라이드+후라이드", 33000, true, null, Arrays.asList(
                메뉴_상품(1L, 1L, 상품("후라이드", 16000)),
                메뉴_상품(1L, 1L, 상품("후라이드", 16000))));
        given(menuRepository.findAllByProductId(any())).willReturn(Arrays.asList(menu));

        // when
        Product expectedProduct = productService.changePrice(UUID.randomUUID(), 변경될_가격_상품_요청);

        // then
        assertAll(
                () -> assertThat(menu.isDisplayed()).isFalse(),
                () -> assertThat(expectedProduct.getPrice()).isEqualTo(new BigDecimal(변경될_가격))
        );
    }

    @DisplayName("변경될 상품 가격 음수거나 null인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10, -100})
    public void changePriceWithNegativePrice(int price) {
        // given
        Product 음수_가격_상품_요청 = 상품_요청("강정치킨", price);

        // when, then
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), 음수_가격_상품_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이 속한 각 메뉴의 가격이 메뉴 상품들의 가격의 합보다 크면 숨김 처리한다")
    @ParameterizedTest
    @CsvSource({"33000,16000","48000,12000"})
    public void changePriceNotDisplay(int menuPrice, int menuProductPrice) {
        // given
        Product 상품_요청 = 상품_요청("후라이드", menuProductPrice);

        Product 기존_상품 = 상품("후라이드", 15000);
        given(productRepository.findById(any())).willReturn(Optional.of(기존_상품));

        MenuProduct 메뉴_상품 = 메뉴_상품(1L, 1L, 상품("후라이드", menuProductPrice));
        Menu 메뉴 = 메뉴("후라이드+후라이드", menuPrice, true, null, Arrays.asList(메뉴_상품, 메뉴_상품));

        given(menuRepository.findAllByProductId(any()))
                .willReturn(Arrays.asList(메뉴, 메뉴));

        // when
        productService.changePrice(UUID.randomUUID(), 상품_요청);

        // then
        assertAll(
                () -> assertThat(메뉴.isDisplayed()).isFalse());
    }

    @DisplayName("모든 상품을 조회한다.")
    @Test
    public void findAll() {
        // given
        Product 후라이드 = 상품("후라이드", 17000);
        given(productRepository.findAll()).willReturn(Arrays.asList(후라이드, 후라이드));

        // when
        List<Product> products = productService.findAll();

        // then
        assertAll(
                () -> assertThat(products.size()).isEqualTo(2)
        );
    }
}
