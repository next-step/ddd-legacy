package kitchenpos.application;

import static kitchenpos.domain.MenuFixture.MenuWithUUIDAndMenuGroup;
import static kitchenpos.domain.MenuGroupFixture.MenuGroupWithUUID;
import static kitchenpos.domain.MenuProductFixture.MenuProductWithProduct;
import static kitchenpos.domain.ProductFixture.Product;
import static kitchenpos.domain.ProductFixture.ProductWithUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    @DisplayName("새로운 상품을 생성할 수 있다.")
    @Nested
    class Create {

        @DisplayName("성공")
        @Test
        void create() {
            // given
            Product request = Product("후라이드 치킨", 15_000);

            // when
            Product savedProduct = productService.create(request);

            // then
            assertThat(savedProduct.getId()).isNotNull();
            assertThat(savedProduct.getName()).isEqualTo("후라이드 치킨");
            assertThat(savedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(15_000).setScale(2));
        }

        @DisplayName("상품 금액은 null 일 수 없다.")
        @Test
        void priceNullException() {
            // given
            Product request = new Product();
            request.setName("후라이드 치킨");
            request.setPrice(null);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 금액은 음수일 수 없다.")
        @Test
        void priceNegativeException() {
            // given
            Product request = Product("후라이드 치킨", -1);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 이름은 null 일 수 없다.")
        @Test
        void nameNullException() {
            // given
            Product request = Product(null, 15_000);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 이름은 욕설을 포함할 수 없다.")
        @Test
        void nameProfanityException() {
            // given
            Product request = Product("ass 후라이드 치킨", 15_000);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("상품의 금액을 변경할 수 있다.")
    @Nested
    class ChangePrice {

        @DisplayName("성공")
        @Test
        void changePrice() {
            // given
            Product 후라이드_치킨 = productRepository.save(ProductWithUUID("후라이드 치킨", 15_000));
            Product request_16000원 = Product("후라이드 치킨", 16_000);

            // when
            Product result = productService.changePrice(
                후라이드_치킨.getId(),
                request_16000원
            );

            // then
            assertThat(result.getId()).isEqualTo(후라이드_치킨.getId());
            assertThat(result.getName()).isEqualTo("후라이드 치킨");
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(16_000).setScale(2));
        }

        @DisplayName("상품이 우선 존재해야 한다.")
        @Test
        void productNotFoundException() {
            // given
            Product request_16000원 = Product("후라이드 치킨", 16_000);

            // when, then
            assertThatThrownBy(() -> productService.changePrice(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                request_16000원
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("상품의 금액이 변한 후, 상품이 속한 메뉴의 금액이 모든 상품의 금액보다 높을 경우 해당 메뉴의 전시(노출)을 중단한다.")
        @Test
        void stopDisplayedIfInvalidPrice() {
            // given
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);

            Product request = Product("콜라", 1_900);

            // when
            productService.changePrice(콜라.getId(), request);

            // then
            assertThat(햄버거_콜라_세트메뉴.isDisplayed()).isTrue();
            assertThat(ID를_통한_메뉴_조회(햄버거_콜라_세트메뉴.getId()).isDisplayed()).isFalse();
        }

        private Menu ID를_통한_메뉴_조회(UUID menuId) {
            return menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        }
    }

    @DisplayName("전체 상품을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Product 후라이드_치킨 = productRepository.save(ProductWithUUID("후라이드 치킨", 15_000));
        Product 양념_치킨 = productRepository.save(ProductWithUUID("양념 치킨", 16_000));

        // when
        List<Product> results = productService.findAll();

        // then
        assertThat(results).usingRecursiveFieldByFieldElementComparator()
            .containsExactly(후라이드_치킨, 양념_치킨);
    }
}
