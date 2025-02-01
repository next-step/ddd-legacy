package kitchenpos.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("ProductService 클래스의")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @DisplayName("create 메소드는")
    @Nested
    class Create {

        @DisplayName("상품의 가격이 존재하지 않으면 예외를 던진다.")
        @Test
        void createWithEmptyPrice() {
            // given
            final Product product = new Product();
            product.setName("product");
            product.setPrice(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @DisplayName("상품의 이름이 존재하지 않으면 예외를 던진다.")
        @Test
        void createWithEmptyName() {
            // given
            final Product product = new Product();
            product.setName(null);
            product.setPrice(BigDecimal.valueOf(10));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @DisplayName("상품의 이름에 욕설이 포함되어 있으면 예외를 던진다.")
        @Test
        void createWithProfanity() {
            // given
            final Product product = new Product();
            product.setName("비속어");
            product.setPrice(BigDecimal.valueOf(10));
            when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @DisplayName("상품을 생성한다.")
        @Test
        void create() {
            // given
            final Product product = new Product();
            product.setName("product");
            product.setPrice(BigDecimal.valueOf(10));
            when(purgomalumClient.containsProfanity(product.getName())).thenReturn(false);

            // when
            final Product createdProduct = productService.create(product);

            // then
            assertNotNull(createdProduct.getId());
            assertEquals(product.getName(), createdProduct.getName());
            assertEquals(product.getPrice(), createdProduct.getPrice());
        }
    }

    @DisplayName("changePrice 메소드는")
    @Nested
    class ChangePrice {

        private Product product;
        private Menu menu;

        @BeforeEach
        void setUp() {
            product = new Product();
            product.setId(UUID.randomUUID());
            product.setName("product");
            product.setPrice(BigDecimal.valueOf(10));
            productRepository.save(product);

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setName("menuGroup");
            menuGroup.setId(UUID.randomUUID());
            menuGroupRepository.save(menuGroup);

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setName("menu");
            menu.setPrice(BigDecimal.valueOf(10));
            menu.setMenuProducts(List.of(menuProduct));
            menu.setMenuGroup(menuGroup);

            menuRepository.save(menu);
        }

        @DisplayName("상품의 가격이 존재하지 않으면 예외를 던진다.")
        @Test
        void changePriceWithEmptyPrice() {
            // given
            product.setPrice(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> productService.changePrice(product.getId(), product));
        }

        @DisplayName("상품을 찾을 수 없으면 예외를 던진다.")
        @Test
        void changePriceWithNonExistProduct() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> productService.changePrice(UUID.randomUUID(), product));
        }

        @DisplayName("상품의 가격을 변경한다.")
        @Test
        void changePrice() {
            // given
            final BigDecimal price = BigDecimal.valueOf(20);
            product.setPrice(price);

            // when
            final Product changedProduct = productService.changePrice(product.getId(), product);

            // then
            assertEquals(price, changedProduct.getPrice());
        }

        @DisplayName("메뉴의 가격이 상품의 가격 합보다 작으면 메뉴를 미노출 처리한다.")
        @Test
        void changePriceWithMenuPriceLessThanProductPriceSum() {
            // given
            Product productRequest = new Product();
            productRequest.setPrice(BigDecimal.valueOf(5));
            menu.setDisplayed(true);
            menuRepository.save(menu);

            // when
            productService.changePrice(product.getId(), productRequest);

            // then
            assertFalse(menuRepository.findById(menu.getId()).get().isDisplayed());
        }
    }

    @DisplayName("findAll 메소드는")
    @Nested
    class FindAll {

        @DisplayName("상품이 존재하지 않으면 빈 목록을 반환한다.")
        @Test
        void findAllWithEmpty() {
            // when
            final List<Product> products = productService.findAll();

            // then
            assertTrue(products.isEmpty());
        }

        @DisplayName("상품이 존재하면 상품 목록을 반환한다.")
        @Test
        void findAll() {
            // given
            final Product product = new Product();
            product.setName("product");
            product.setPrice(BigDecimal.valueOf(10));
            productService.create(product);

            // when
            final List<Product> products = productService.findAll();

            // then
            assertFalse(products.isEmpty());
        }
    }
}
