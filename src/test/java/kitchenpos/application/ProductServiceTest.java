package kitchenpos.application;

import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.MenuProductBuilder;
import kitchenpos.builder.ProductBuilder;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.mock.MockMenuRepository;
import kitchenpos.mock.MockProductRepository;
import kitchenpos.mock.MockPurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest {
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new MockProductRepository();
        menuRepository = new MockMenuRepository();
        purgomalumClient = new MockPurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("이름과 가격으로 제품을 추가한다")
    @Test
    void create() {
        final Product expected = ProductBuilder.newInstance()
                .build();

        final Product actual = productService.create(expected);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @DisplayName("가격은 필수고, 0 이상이어야 한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-16000")
    void create(final BigDecimal price) {
        final Product expected = ProductBuilder.newInstance()
                .setPrice(price)
                .build();

        assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이름은 필수고, 비속어가 포함되지 않아야 한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"비속어", "욕설이 포함된 이름"})
    void create(final String name) {
        final Product expected = ProductBuilder.newInstance()
                .setName(name)
                .build();

        assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 제품의 식별자와 바꿀 가격으로 제품의 가격을 바꾼다")
    @Test
    void changePrice() {
        Product product = productRepository.save(ProductBuilder.newInstance()
                .setPrice(16_000L)
                .build()
        );
        Product expected = ProductBuilder.newInstance()
                .setPrice(17_000L)
                .build();

        Product actual = productService.changePrice(product.getId(), expected);

        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @DisplayName("바꿀 가격은 필수고, 0 이상이어야 한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-16000")
    void changePrice(final BigDecimal price) {
        Product product = productRepository.save(ProductBuilder.newInstance()
                .build()
        );
        Product expected = ProductBuilder.newInstance()
                .setPrice(price)
                .build();

        assertThatThrownBy(() -> productService.changePrice(product.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("제품의 식별자로 특정 제품을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void changePrice(final UUID productId) {
        Product expected = ProductBuilder.newInstance()
                .build();

        assertThatThrownBy(() -> productService.changePrice(productId, expected))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가격을 바꾼 후 특정 제품을 포함한 모든 메뉴를 조회하여, 메뉴의 가격이 메뉴가 포함한 모든 제품의 (가격 * 수량)의 합보다 크면, 메뉴를 숨긴다")
    @ParameterizedTest
    @CsvSource({"14000,false", "15000,true"})
    void changePrice(BigDecimal changePrice, boolean expected) {
        Product product = productRepository.save(ProductBuilder.newInstance()
                .setPrice(16_000L)
                .build()
        );
        Menu actual = menuRepository.save(MenuBuilder.newInstance()
                .setMenuProducts(MenuProductBuilder.newInstance()
                        .setProduct(product)
                        .setQuantity(2L)
                        .build()
                )
                .setPrice(30_000L)
                .setDisplayed(true)
                .build()
        );

        productService.changePrice(product.getId(), ProductBuilder.newInstance()
                .setPrice(changePrice)
                .build()
        );

        assertThat(menuRepository.findById(actual.getId())
                .get()
                .isDisplayed()
        ).isEqualTo(expected);
    }

    @DisplayName("제품 전체 목록을 조회한다")
    @Test
    void findAll() {
        final int expected = 2;

        IntStream.range(0, expected)
                .mapToObj(index -> ProductBuilder.newInstance().build())
                .forEach(productRepository::save);

        assertThat(productService.findAll()).hasSize(expected);
    }
}
