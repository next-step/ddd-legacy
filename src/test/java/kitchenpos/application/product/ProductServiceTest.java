package kitchenpos.application.product;

import kitchenpos.application.ProductService;
import kitchenpos.application.menu.MenuTestFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@DisplayName("Application: 상품 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;


    @Nested
    @DisplayName("상품을 등록(생성) 할 수 있다.")
    class createProduct {

        @Test
        @DisplayName("정상적으로 등록(생성)할 수 있다")
        void case_1() {
            // given
            Product response = ProductTestFixture.aProduct("레드 한마리", 1000L);

            // when
            when(purgomalumClient.containsProfanity(response.getName())).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(response);
            Product createdProduct = productService.create(response);

            // then
            assertEquals(response.getName(), createdProduct.getName());
        }

        @DisplayName("가격이 없거나 0보다 작은 경우 등록(생성)할 수 없다..")
        @ParameterizedTest(name = "가격이 {0}인 경우")
        @ValueSource(longs = -1L)
        @NullSource
        void case_2(Long price) {
            // given
            Product response = ProductTestFixture.aProduct("레드 한마리", price);
            // when then
            assertThrows(IllegalArgumentException.class, () -> {
                productService.create(response);
            });

        }

        @DisplayName("상품명이 없거나 욕설이 포함된 경우 등록(생성)할 수 없다.")
        @ParameterizedTest(name = "상품명이 {0}인 경우")
        @ValueSource(strings = {"민트초코 맛 레드 한마리", "파인애플 새우볼"})
        @NullSource
        void case_2(String name) {
            // given
            Product response = ProductTestFixture.aProduct(name, 1000L);
            // when
            lenient().when(purgomalumClient.containsProfanity(response.getName())).thenReturn(true);
            // then
            assertThrows(IllegalArgumentException.class, () -> {
                productService.create(response);
            });
        }

    }

    @Nested
    @DisplayName("상품의 가격을 변경(수정) 할 수 있다.")
    class changeProductPrice {
        @Test
        @DisplayName("정상적으로 가격을 변경(수정)할 수 있다.")
        void case_1() {
            // given
            Product response = ProductTestFixture.aProduct("레드 한마리", 1000L);

            // when
            when(productRepository.findById(any())).thenReturn(of(response));
            when(menuRepository.findAllByProductId(any())).thenReturn(java.util.Collections.emptyList());

            Product responseProduct = productService.changePrice(response.getId(), response);

            // then
            assertEquals(response.getPrice(), responseProduct.getPrice());
        }

        @DisplayName("가격이 없거나 0보다 작은 경우 변경(수정)할 수 없다.")
        @ParameterizedTest(name = "가격이 {0}인 경우")
        @ValueSource(longs = -1L)
        @NullSource
        void case_2(Long price) {
            // given
            Product response = ProductTestFixture.aProduct("레드 한마리", price);
            // when && then
            assertThrows(IllegalArgumentException.class, () -> {
                productService.changePrice(response.getId(), response);
            });
        }

        @Test
        @DisplayName("상품이 존재하지 않을 경우 변경(수정)할 수 없다.")
        void case_3() {
            // given
            Product response = ProductTestFixture.aProduct("레드 한마리", 1000L);
            // when
            when(productRepository.findById(any())).thenReturn(empty());
            // then
            assertThrows(NoSuchElementException.class, () -> {
                productService.changePrice(response.getId(), response);
            });
        }

        @Test
        @DisplayName("메뉴의 가격이 메뉴 상품들의 총 가격보다 클 경우 메뉴가 표시되지 않는다.")
        void case_4() {
            // given
            Product product = ProductTestFixture.aProduct("레드 한마리", 10000L);
            MenuGroup menuGroup = MenuTestFixture.aMenuGroup();
            MenuProduct menuProduct = MenuTestFixture.aMenuProduct(1L, product, 1L);
            Menu menu = MenuTestFixture.aMenu("그린 세트", 20000L, menuGroup, Collections.singletonList(menuProduct));

            // when
            when(productRepository.findById(any())).thenReturn(of(product));
            when(menuRepository.findAllByProductId(any(UUID.class))).thenReturn(Collections.singletonList(menu));
            Product responseProduct = productService.changePrice(product.getId(), product);

            // then
            assertEquals(product.getPrice(), responseProduct.getPrice());
            assertFalse(menu.isDisplayed()); // 메뉴가 표시되지 않는지 확인
        }
    }
}
