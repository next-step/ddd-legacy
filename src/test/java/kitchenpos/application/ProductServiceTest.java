package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import kitchenpos.domain.FakeMenuRepository;
import kitchenpos.domain.FakeProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakeProfanityDetectClient;
import kitchenpos.infra.ProfanityDetectClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();

    private final ProductRepository productRepository = new FakeProductRepository();

    private final ProfanityDetectClient profanityDetectClient = new FakeProfanityDetectClient();

    // SUT

    private final ProductService productService = new ProductService(
        productRepository,
        menuRepository,
        profanityDetectClient
    );

    @DisplayName("생성")
    @Nested
    class Hbytrwnk {

        @DisplayName("유효한 이름으로 상품을 생성할 수 있다.")
        @ValueSource(strings = {
            "stand", "dissatisfy", "funeral", "omit", "bind",
            "somehow", "photography", "skin", "collect", "steer",
        })
        @ParameterizedTest
        void tvjjmpqr(final String name) {
            // given
            final Product requestProduct = new Product();
            requestProduct.setName(name);
            requestProduct.setPrice(BigDecimal.valueOf(10000));

            // when
            final Product product = productService.create(requestProduct);

            // then
            assertThat(product.getName()).isEqualTo(name);
        }

        @DisplayName("이름은 설정하지 않거나 빈 문자열일 수 없다.")
        @NullAndEmptySource
        @ParameterizedTest
        void kuqkjicz(final String name) {
            // given
            final Product requestProduct = new Product();
            requestProduct.setName(name);
            requestProduct.setPrice(BigDecimal.valueOf(10000));

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(requestProduct));
        }

        @DisplayName("이름은 비속어를 포함할 수 없다.")
        @ValueSource(strings = {
            "holiday", "bed", "anxious", "everyday", "reach",
            "private holiday", "courage bed", "about anxious", "knee everyday", "number reach",
            "holiday roll", "bed avenue", "anxious needle", "everyday chairman", "reach cape",
            "interrupt holiday sauce", "explore bed radio", "return anxious motherly",
            "entertain everyday cause", "separate reach they",
        })
        @ParameterizedTest
        void pldkawhd(final String name) {
            // given
            final Product requestProduct = new Product();
            requestProduct.setName(name);
            requestProduct.setPrice(BigDecimal.valueOf(10000));

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(requestProduct));
        }

        @DisplayName("유효한 가격으로 상품을 생성할 수 있다.")
        @ValueSource(longs = {
            804678989, 1239447717, 1147921460, 703315726, 656698661,
            1125669271, 154820142, 944887592, 1003898244, 1390965447,
        })
        @ParameterizedTest
        void yarzrqyy(final long price) {
            // given
            final BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);

            final Product requestProduct = new Product();
            requestProduct.setName("ought");
            requestProduct.setPrice(bigDecimalPrice);

            // when
            final Product product = productService.create(requestProduct);

            // then
            assertThat(product.getPrice()).isEqualTo(bigDecimalPrice);
        }

        @DisplayName("가격은 음수일 수 없다.")
        @ValueSource(longs = {
            -249426902, -914958484, -1150897566, -2145517978, -1890425103,
            -1846707062, -445918517, -2056206535, -923533520, -167058921,
        })
        @ParameterizedTest
        void sugigycp(final long price) {
            // given
            final BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);

            final Product requestProduct = new Product();
            requestProduct.setName("slip");
            requestProduct.setPrice(bigDecimalPrice);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(requestProduct));
        }
    }

    @DisplayName("가격 변경")
    @Nested
    class Jlccquig {

        @DisplayName("상품 가격을 변경할 수 있다.")
        @ValueSource(longs = {
            1745026271, 506427010, 579969879, 848846764, 113279252,
            1218368086, 972223887, 799241116, 791758769, 22362211,
        })
        @ParameterizedTest
        void lxjtpjbl(final long price) {
            // given
            final BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);

            final Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setName("set");
            product.setPrice(BigDecimal.valueOf(10000));
            final Product savedProduct = productRepository.save(product);

            final Product requestProduct = new Product();
            requestProduct.setPrice(bigDecimalPrice);

            // when
            final Product updatedProduct = productService.changePrice(
                savedProduct.getId(),
                requestProduct
            );

            // then
            assertThat(updatedProduct.getPrice()).isEqualTo(bigDecimalPrice);

            final Product foundProduct = productRepository.findById(savedProduct.getId())
                .orElse(null);
            assertThat(foundProduct).isNotNull();
            assertThat(foundProduct.getPrice()).isEqualTo(bigDecimalPrice);
        }

        @DisplayName("상품 가격을 음수로 변경할 수 없다.")
        @ValueSource(longs = {
            -1739790547, -2133620232, -1606999432, -860456090, -1109350517,
            -2048064491, -1910998867, -668602241, -244925836, -131156125,
        })
        @ParameterizedTest
        void cdfltrvq(final long price) {
            // given
            final BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);

            final Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setName("set");
            product.setPrice(BigDecimal.valueOf(10000));
            final Product savedProduct = productRepository.save(product);

            final Product requestProduct = new Product();
            requestProduct.setPrice(bigDecimalPrice);

            // when / then
            assertThatIllegalArgumentException().isThrownBy(() -> productService.changePrice(
                savedProduct.getId(),
                requestProduct
            ));
        }

        @DisplayName("상품의 가격이 변경된 경우 메뉴의 가격이 메뉴에 포함된 모든 상품 가격의 합보다 크면 메뉴가 노출되지 않도록 변경된다.")
        @Test
        void ytsshrhy() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setName("though");
            product1.setPrice(BigDecimal.valueOf(50000));
            productRepository.save(product1);

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setName("perhaps");
            product2.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product2);

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(30000));
            menu.setDisplayed(true);
            final Menu savedMenu = menuRepository.save(menu);

            final Product requestProduct = new Product();
            requestProduct.setPrice(BigDecimal.valueOf(10000));

            // when
            productService.changePrice(product1.getId(), requestProduct);

            // then
            final Menu foundMenu = menuRepository.findById(savedMenu.getId())
                .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.isDisplayed()).isFalse();
        }

        @DisplayName("상품의 가격이 변경된 경우 메뉴의 가격이 메뉴에 포함된 모든 상품 가격의 합보다 크지 않으면 메뉴 노출 상태는 변경되지 않는다.")
        @Test
        void kksmstdg() {
            // given
            final Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setName("fade");
            product1.setPrice(BigDecimal.valueOf(50000));
            productRepository.save(product1);

            final Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setName("only");
            product2.setPrice(BigDecimal.valueOf(10000));
            productRepository.save(product2);

            final MenuProduct menuProduct1 = new MenuProduct();
            menuProduct1.setProduct(product1);
            menuProduct1.setProductId(product1.getId());
            menuProduct1.setQuantity(1);
            menuProduct1.setSeq(1L);

            final MenuProduct menuProduct2 = new MenuProduct();
            menuProduct2.setProduct(product2);
            menuProduct2.setProductId(product2.getId());
            menuProduct2.setQuantity(1);
            menuProduct2.setSeq(1L);

            final List<MenuProduct> menuProducts = new ArrayList<>();
            menuProducts.add(menuProduct1);
            menuProducts.add(menuProduct2);

            final Menu menu = new Menu();
            menu.setId(UUID.randomUUID());
            menu.setMenuProducts(menuProducts);
            menu.setPrice(BigDecimal.valueOf(30000));
            menu.setDisplayed(true);
            final Menu savedMenu = menuRepository.save(menu);

            final Product requestProduct = new Product();
            requestProduct.setPrice(BigDecimal.valueOf(40000));

            // when
            productService.changePrice(product1.getId(), requestProduct);

            // then
            final Menu foundMenu = menuRepository.findById(savedMenu.getId())
                .orElse(null);
            assertThat(foundMenu).isNotNull();
            assertThat(foundMenu.isDisplayed()).isTrue();
        }
    }

    @DisplayName("목록 조회")
    @Nested
    class Risgpuzj {

        @DisplayName("상품을 생성한 후 모두 조회할 수 있다.")
        @ValueSource(ints = {
            6, 18, 7, 23, 30,
            8, 2, 21, 31, 20,
        })
        @ParameterizedTest
        void gmucnnhq(final int size) {
            // given
            IntStream.range(0, size)
                .forEach(n -> {
                    final Product product = new Product();
                    product.setName(String.valueOf(n));
                    product.setPrice(BigDecimal.valueOf(10000));
                    productService.create(product);
                });

            // when
            final List<Product> products = productService.findAll();

            // then
            assertThat(products).hasSize(size);
        }

        @DisplayName("상품이 없는 상태에서 모두 조회시 빈 list가 반환되어야 한다.")
        @Test
        void svdtqezm() {
            // when
            final List<Product> products = productService.findAll();

            // then
            assertThat(products).isEmpty();
        }
    }
}
