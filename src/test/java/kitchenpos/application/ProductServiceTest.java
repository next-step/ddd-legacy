package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @Test
    @DisplayName("상품을 생성한다.")
    void createProduct() {
        Product product = ProductFixture.setProduct("새상품", "10000");

        when(purgomalumClient.containsProfanity(any()))
                .thenReturn(false);
        when(productRepository.save(any()))
                .thenReturn(product);

        Product newProduct = productService.create(product);

        assertAll(
                () -> assertThat(newProduct.getId()).isInstanceOf(UUID.class),
                () -> assertThat(newProduct.getName()).isEqualTo(product.getName()),
                () -> assertThat(newProduct.getPrice()).isEqualTo(product.getPrice())
        );
    }

    @Test
    @DisplayName("조회시 모든 상품을 나타낸다.")
    void showAllProducts() {
        Product product1 = ProductFixture.setProduct("새상품", "10000");
        Product product2 = ProductFixture.setProduct("새상품2", "10000");
        List<Product> productList = List.of(product1, product2);

        when(productRepository.findAll())
                .thenReturn(productList);

        List<Product> productListResult = productService.findAll();

        assertAll(
                () -> assertThat(productListResult.size()).isEqualTo(productList.size()),
                () -> assertThat(productListResult).containsExactly(product1, product2)
        );
    }


    @DisplayName("상품생성시 가격은 필수로 입력되어야 한다")
    @ParameterizedTest
    @NullSource
    void throwExceptionWhenPriceIsNull(String price){
        Product product = ProductFixture.setProduct("새상품2", price);
        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품생성시 가격은 0이상의 양수 이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1000","-2000"})
    void throwExceptionWhenPriceIsNegative(String price){
        Product product = ProductFixture.setProduct("새상품2", price);
        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격변경시 가격은 필수이어야 한다.")
    @ParameterizedTest
    @NullSource
    void throwExceptionWhenChangePriceIsNull(String price){
        Product product = ProductFixture.setProduct("새상품2", price);
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격변경시 가격은 0이상 양수이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1000","-2000"})
    void throwExceptionWhenChangePriceIsNegative(String price){
        Product product = ProductFixture.setProduct("새상품2", price);
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격 변경시 해당 상품을 쓴 메뉴의 가격보다 해당 메뉴상품의 가격이 크다면 메뉴를 숨긴다.")
    @ParameterizedTest
    @ValueSource(strings = {"10000","20000"})
    void hideMenuWhenMenuProductTotalPriceBiggerThanMenuPrice(String price){
        Product product = ProductFixture.setProduct("제품", "5000");
        MenuProduct menuProduct = MenuProductFixture.setMenuProduct(product, 1, 1);
        Menu menu = MenuFixture.setMenuGroup(
                MenuGroupFixture.setMenuGroup("메인디쉬"),
                "메인디쉬", "5000",      List.of(menuProduct)
        );

        given(productRepository.findById(any())).willReturn()


    }
}
