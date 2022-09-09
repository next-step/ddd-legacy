package kitchenpos.application;

import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private PurgomalumClient purgomalumClient;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private ProductService productService;


    @DisplayName("제품을 등록한다.")
    @Nested
    class CreateTest {

        @DisplayName("제품이 등록된다.")
        @Test
        void createdProduct() {
            // given
            final Product request = ProductFixture.createRequest("후라이드 치킨", BigDecimal.valueOf(15_000));

            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
            given(productRepository.save(any())).will(AdditionalAnswers.returnsFirstArg());

            // when
            final Product result = productService.create(request);

            // then
            assertAll(() -> {
                assertThat(result.getId()).isNotNull();
                assertThat(result.getName()).isEqualTo("후라이드 치킨");
                assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(15_000));
            });
        }

        @DisplayName("제품의 이름은 비어있을 수 없다.")
        @Test
        void null_name() {
            // given
            final Product request = ProductFixture.createRequest(null, BigDecimal.valueOf(15_000));

            // then
            assertThatThrownBy(() -> productService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("제품의 이름은 욕설, 외설 및 기타 워치않는 용어에 해당할 수 없다.")
        @Test
        void negative_name() {
            // given
            final Product request = ProductFixture.createRequest("후라이드 치킨", BigDecimal.valueOf(15_000));

            given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

            // then
            assertThatThrownBy(() -> productService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("제품의 가격은 0원 이상이여야 한다.")
        @Test
        void negative_price() {
            // given
            final Product request = ProductFixture.createRequest("후라이드 치킨", BigDecimal.valueOf(-15_000));

            // then
            assertThatThrownBy(() -> productService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("제품의 가격을 수정한다.")
    @Nested
    class ChangePriceTest {

        @DisplayName("제품의 가격이 수정된다.")
        @Test
        void changePrice() {
            // given
            Product request = ProductFixture.create("후라이드 치킨", BigDecimal.valueOf(20_000));

            given(productRepository.findById(any())).willReturn(Optional.of(request));
            given(menuRepository.findAllByProductId(any())).willReturn(Collections.emptyList());

            // when
            Product product = productService.changePrice(request.getId(), request);

            // then
            assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(20_000));
        }

        @DisplayName("수정할 가격은 0원 이상이여야 한다.")
        @Test
        void negative_price() {
            // given
            Product request = ProductFixture.create("후라이드 치킨", BigDecimal.valueOf(-15_000));

            // then
            assertThatThrownBy(() -> productService.changePrice(request.getId(), request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경하려는 제품을 포함하는 메뉴의 가격이 (변경된 구성품목 가격 x 구성품목의 개수)의 총합보다 클 경우 메뉴를 숨긴다.")
        @Test
        void hideMenu() {
            // given
            final Product request = ProductFixture.create("후라이드 치킨", BigDecimal.valueOf(10_000));
            final Menu menu = MenuFixture.create("후라이드 치킨",
                    BigDecimal.valueOf(15_000),
                    true,
                    MenuGroupFixture.createDefault(),
                    List.of(MenuProductFixture.of(request)));

            given(productRepository.findById(any())).willReturn(Optional.of(request));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            productService.changePrice(request.getId(), request);

            // then
            assertThat(menu.isDisplayed()).isFalse();
        }
    }
}
