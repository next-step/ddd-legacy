package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.*;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Nested
    class 상품_생성_경우 {
        @Test
        void 정상적으로_생성된다() {
            // given
            final Product request = product(null, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);

            // when
            final Product response = productService.create(request);

            // then
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getPrice()).isEqualTo(request.getPrice());
        }

        @Test
        void 음수_가격이_입력되면_예외가_발생한다() {
            // given
            final Product request = product(null, DEFAULT_PRODUCT_NAME, BigDecimal.valueOf(-10_000L));

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @ParameterizedTest(name = "입력 값 `{0}`")
        @ValueSource(strings = {"bitch", "fuck"})
        void 욕설이_포함된_이름이_입력되면_예외가_발생한다(final String name) {
            // given
            final Product request = product(null, name, DEFAULT_PRODUCT_PRICE);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @Test
        void 이름이_NULL이면_예외가_발생한다() {
            // given
            final Product request = product(null, null, DEFAULT_PRODUCT_PRICE);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @Test
        void 가격이_NULL이면_예외가_발생한다() {
            // given
            final Product request = product(null, DEFAULT_PRODUCT_NAME, null);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }
    }

    @Nested
    class 상품_가격_변경_경우 {
        @Test
        void 존재하지_않는_상품의_가격을_변경하면_예외가_발생한다() {
            // given
            final Product request = product();

            // when & then
            assertThatThrownBy(() -> productService.changePrice(request.getId(), request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 음수_가격이_입력되면_예외가_발생한다() {
            // given
            final Product request = productService.create(product(null, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE));

            final Product updateRequest = new Product();
            updateRequest.setPrice(BigDecimal.valueOf(-20_000L));

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.changePrice(request.getId(), updateRequest));
        }

        @Test
        void 메뉴에_포함되지_않은_상품도_가격을_정상적으로_변경한다() {
            // given
            final Product request = productService.create(product(null, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE));

            final Product updateRequest = new Product();
            updateRequest.setPrice(BigDecimal.valueOf(20_000L));

            // when
            final Product response = productService.changePrice(request.getId(), updateRequest);

            // then
            assertThat(response.getId()).isEqualTo(request.getId());
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getPrice()).isEqualTo(request.getPrice());
        }

        @Test
        void 메뉴_구성_상품의_가격_변경_시_메뉴_가격이_구성_상품_총_가격보다_높으면_메뉴가_숨김_처리된다() {
            // given
            final Product request = productService.create(product(null, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE));
            final MenuGroup menuGroup = menuGroupRepository.save(menuGroup());
            final Menu menu = menuRepository.save(menu(createMenuId(), DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, menuGroup, List.of(menuProduct(seq(), DEFALUT_QUANTITY, request)), DEFAULT_DISPLAYED));

            final Product updateRequest = new Product();
            updateRequest.setPrice(BigDecimal.valueOf(8_000L));

            // when
            productService.changePrice(request.getId(), updateRequest);

            // then
            final Menu response = menuRepository.findById(menu.getId()).orElseThrow(NoSuchElementException::new);
            assertThat(response.isDisplayed()).isFalse();
        }
    }

    @Nested
    class 상품_조회_경우 {
        @Test
        void 전체_상품을_조회하면_생성된_모든_상품이_반환된다() {
            // given
            productService.create(product(null, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE));
            productService.create(product(null, "후라이드 치킨", BigDecimal.valueOf(12_000L)));

            // when
            final List<Product> response = productService.findAll();

            // then
            assertThat(response).hasSize(2);
        }
    }

}
