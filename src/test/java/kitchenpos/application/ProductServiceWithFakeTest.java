package kitchenpos.application;


import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.fixture.fake.FakeProfanityClient;
import kitchenpos.fixture.fake.InMemoryMenuRepository;
import kitchenpos.fixture.fake.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceWithFakeTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private ProfanityClient profanityClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        profanityClient = new FakeProfanityClient();
        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }

    @DisplayName("제품 등록")
    @Nested
    class CreateTest {

        @DisplayName("등록 성공")
        @Test
        void createdProduct() {
            // when
            final Product request = ProductFixture.createRequest("후라이드 치킨", 15_000L);
            final Product result = productService.create(request);

            // then
            assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("후라이드 치킨"),
                () -> assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(15_000L))
            );
        }

        @DisplayName("이름은 비어있을 수 없다.")
        @Test
        void null_name() {
            // when
            final Product request = ProductFixture.createRequest(null, 15_000L);

            // then
            assertThatThrownBy(() -> productService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 욕설, 외설 및 기타 원치않는 용어에 해당할 수 없다.")
        @Test
        void negative_name() {
            // when
            final Product request = ProductFixture.createRequest("욕설", 15_000L);

            // then
            assertThatThrownBy(() -> productService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("가격은 0원 이상이여야 한다.")
        @Test
        void negative_price() {
            // when
            final Product request = ProductFixture.createRequest("후라이드 치킨", -15_000L);

            // then
            assertThatThrownBy(() -> productService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("제품의 가격 수정")
    @Nested
    class ChangePriceTest {

        @DisplayName("수정 성공")
        @Test
        void changePrice() {
            // given
            final Product product = productRepository.save(ProductFixture.create("후라이드 치킨", 15_000L));
            final Product request = ProductFixture.createPriceRequest(20_000L);

            // when
            final Product result = productService.changePrice(product.getId(), request);

            // then
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(20_000));
        }

        @DisplayName("가격은 0원 이상이여야 한다.")
        @Test
        void negative_price() {
            // given
            final Product product = productRepository.save(ProductFixture.create("후라이드 치킨", 15_000L));
            final Product request = ProductFixture.createPriceRequest(-15_000L);

            // then
            assertThatThrownBy(() -> productService.changePrice(product.getId(), request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경하려는 제품을 포함하는 메뉴의 가격이 (변경된 구성품목 가격 x 구성품목의 개수)의 총합보다 클 경우 메뉴를 숨긴다.")
        @Test
        void hideMenu() {
            // given
            final Product product = productRepository.save(ProductFixture.create("후라이드 치킨", 15_000L));
            final Menu menu = MenuFixture.create("후라이드 치킨",
                    BigDecimal.valueOf(15_000),
                    true,
                    MenuGroupFixture.createDefault(),
                    List.of(MenuProductFixture.of(product)));
            menuRepository.save(menu);

            final Product request = ProductFixture.createPriceRequest(10_000L);

            // when
            productService.changePrice(product.getId(), request);

            // then
            Menu result = menuRepository.findById(menu.getId()).get();
            assertThat(result.isDisplayed()).isFalse();
        }
    }
}
