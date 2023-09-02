package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.ProductTestFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    MenuRepository menuRepository;

    @Mock
    PurgomalumClient purgomalumClient;

    @Test
    void 상품을_생성한다() {
        Product product = ProductTestFixture.createProduct("test", BigDecimal.valueOf(200));
        given(productRepository.save(any())).willReturn(product);

        Product actual = productService.create(product);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 상품_생성_시_이름이_비어있으면_예외가_발생한다() {
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(100));

        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_시_금액이_0_이면_예외가_발생한다() {
        Product request = new Product();
        request.setName("test");
        request.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_시_상품_이름에_비속어가_들어가면_예외가_발생한다() {
        Product request = new Product();
        request.setName("test");
        request.setPrice(BigDecimal.valueOf(100));

        given(purgomalumClient.containsProfanity(any())).willReturn(true);
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품이_존재하지_않을_경우_예외가_발생한다() {
        UUID productId = UUID.randomUUID();

        Product request = new Product();
        request.setName("test");
        request.setPrice(BigDecimal.valueOf(200));

        given(productRepository.findById(productId)).willThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> productService.changePrice(productId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_수정_시_금액이_0_미만이면_예외가_발생한다() {
        Product product1 = ProductTestFixture.createProduct("상품1", BigDecimal.valueOf(100));

        Product request = new Product();
        request.setName("test");
        request.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> productService.changePrice(product1.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 해당_상품을_포함하는_메뉴의_가격이_메뉴에_포함_된_상품_가격_x_상품_수량의_합보다_크면_메뉴_노출이_비활성화_된다() {
        Product product1 = ProductTestFixture.createProduct("상품1", BigDecimal.valueOf(100));
        MenuProduct menuProduct = ProductTestFixture.createMenuProduct(product1, 1L, 1L);
        Menu menu = ProductTestFixture.createMenu("메뉴명", BigDecimal.valueOf(400), UUID.randomUUID(), true, menuProduct);
        Product request = new Product();
        request.setName("test");
        request.setPrice(BigDecimal.valueOf(300));

        given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
        given(menuRepository.findAllByProductId(product1.getId())).willReturn(Arrays.asList(menu));
        Product actual = productService.changePrice(product1.getId(), request);

        assertThat(menu.isDisplayed()).isFalse();
        assertThat(actual.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    void 상품의_전체_목록을_조회한다() {
        Product product1 = ProductTestFixture.createProduct("상품1", BigDecimal.valueOf(100));
        Product product2 = ProductTestFixture.createProduct("test", BigDecimal.valueOf(200));
        Product product3 = ProductTestFixture.createProduct("test", BigDecimal.valueOf(200));

        given(productRepository.findAll()).willReturn(Arrays.asList(product1, product2, product3));

        assertThat(productService.findAll()).hasSize(3);
    }
}
