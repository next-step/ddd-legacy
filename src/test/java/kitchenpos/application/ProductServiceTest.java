package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("상품 생성")
    class create {

        @Test
        @DisplayName("상품 생성 성공")
        void success() {
            //given
            Product request = ProductTestFixture.createProductRequest("후라이드치킨", 20000L);
            Product product = ProductTestFixture.createProduct("후라이드치킨", 20000L);

            //when
            Product response = productService.create(request);

            //then
            assertThat(response.getId()).isNotNull();
            assertThat(product.getPrice()).isEqualTo(response.getPrice());
            assertThat(product.getName()).isEqualTo(response.getName());
        }

        @Test
        @DisplayName("상품의 이름에 비속어를 넣을 수 없다.")
        void failBecauseOfProfanity() {
            //given
            Product request = ProductTestFixture.createProductRequest("비속어치킨", 20000L);

            //when
            assertThatThrownBy(() -> productService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상품 가격 수정 ")
    class changePrice {

        Product product;
        Menu menu;

        @BeforeEach
        void setUp() {
            product = ProductTestFixture.createProduct("양념치킨", 22000L);
            productRepository.save(product);

            MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("양념치킨", 22000L, true, List.of(menuProduct));
            menuRepository.save(menu);
        }

        @Test
        @DisplayName("상품 가격 수정 성공")
        void changePrice() {

            //given
            Product request = ProductTestFixture.createProductRequest(23000L);

            //when
            productService.changePrice(product.getId(), request);

            //then
            Product responseProduct = productRepository.findById(product.getId()).get();
            assertThat(responseProduct.getPrice()).isEqualTo(request.getPrice());

            Menu response = menuRepository.findById(menu.getId()).get();
            assertThat(response.isDisplayed()).isTrue();

        }

        @Test
        @DisplayName("상품 가격을 수정했지만 메뉴보다 금액이 낮아서 메뉴가 숨겨진다.")
        void changePriceAndMenuHide() {
            //given
            Product request = ProductTestFixture.createProductRequest(18000L);

            //when
            productService.changePrice(product.getId(), request);

            //then
            Product responseProduct = productRepository.findById(product.getId()).get();
            assertThat(responseProduct.getPrice()).isEqualTo(request.getPrice());

            Menu response = menuRepository.findById(menu.getId()).get();
            assertThat(response.isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("없는 상품의 가격을 수정할 수 없다.")
        void canNotChangeNoProduct() {
            //given
            Product request = ProductTestFixture.createProductRequest(23000L);

            //when then
            assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), request))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("상품 조회")
    class find {

        @Test
        @DisplayName("등록되어 있는 모든 상품 조회")
        void findAll() {

            //given
            Product product1 = ProductTestFixture.createProduct("후라이드치킨", 17000L);
            Product product2 = ProductTestFixture.createProduct("양념치킨", 18000L);
            productRepository.save(product1);
            productRepository.save(product2);

            //when
            List<Product> response = productService.findAll();

            //then
            assertThat(response).hasSize(2);
            assertThat(response)
                    .filteredOn(Product::getId, product1.getId())
                    .containsExactly(product1);
            assertThat(response)
                    .filteredOn(Product::getId, product2.getId())
                    .containsExactly(product2);
        }
    }
}