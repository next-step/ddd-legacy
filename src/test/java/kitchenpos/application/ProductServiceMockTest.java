package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.fixture.MenuFixture.메뉴_생성;
import static kitchenpos.fixture.ProductFixture.상품_생성;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceMockTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @InjectMocks
    private ProductService productService;

    @DisplayName("상품을 생성한다")
    @Test
    void create() {
        //given
        Product product = 상품_생성("피자", BigDecimal.valueOf(20_000));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        Product createdProduct = productService.create(product);

        //then
        then(purgomalumClient).should(times(1)).containsProfanity(any());
        then(productRepository).should(times(1)).save(any());
        assertThat(createdProduct.getName()).isEqualTo(product.getName());
        assertThat(createdProduct.getPrice()).isEqualTo(product.getPrice());
    }

    @DisplayName("상품을 생성 시, 상품 이름이 null 혹은 빈 값인 경우 생성을 실패한다")
    @ParameterizedTest
    @NullSource
    void create_name_exception(String name) {
        //given
        Product product = 상품_생성(name, BigDecimal.valueOf(20_000));

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품을 생성 시, 상품 이름에 부적절한 내용이 포함된 경우 생성을 실패한다")
    @Test
    void create_profanity_name_exception() {
        //given
        Product product = 상품_생성("피자", BigDecimal.valueOf(20_000));
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품을 생성 시, 상품 가격이 0원 미만인 경우 생성을 실패한다")
    @Test
    void create_price_exception() {
        //given
        Product product = 상품_생성("피자", BigDecimal.valueOf(-1));

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 가격을 변경 한다")
    @Test
    void changePrice() {
        //given
        Product originProduct = 상품_생성("피자", BigDecimal.valueOf(20_000));
        when(productRepository.findById(any())).thenReturn(Optional.of(originProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(Collections.emptyList());

        //when
        Product changeProduct = 상품_생성("피자", BigDecimal.valueOf(30_000));
        Product changedProduct = productService.changePrice(UUID.randomUUID(), changeProduct);

        //then
        then(productRepository).should(times(1)).findById(any());
        then(menuRepository).should(times(1)).findAllByProductId(any());
        assertThat(changeProduct.getPrice()).isEqualTo(changedProduct.getPrice());
    }

    @DisplayName("상품 가격을 변경 시, 메뉴 가격이 포함된 상품 가격의 합을 초과하면 메뉴 노출 여부를 미노출로 변경한다.")
    @Test
    void changePrice_menu() {
        //given
        Product originProduct = 상품_생성("피자", BigDecimal.valueOf(50_000));
        Menu menu = 메뉴_생성("메뉴A", BigDecimal.valueOf(50_000), true, UUID.randomUUID(), List.of(originProduct));
        when(productRepository.findById(any())).thenReturn(Optional.of(originProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(Collections.singletonList(menu));

        //when
        Product changeProduct = 상품_생성("피자", BigDecimal.valueOf(30_000));
        Product changedProduct = productService.changePrice(UUID.randomUUID(), changeProduct);

        //then
        then(productRepository).should(times(1)).findById(any());
        then(menuRepository).should(times(1)).findAllByProductId(any());
        assertThat(changeProduct.getPrice()).isEqualTo(changedProduct.getPrice());
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("존재하지 않는 상품의 가격 변경 시, 변경을 실패한다")
    @Test
    void changePrice_product_exception() {
        //given
        UUID uuid = UUID.randomUUID();
        Product product = 상품_생성("피자", BigDecimal.valueOf(1_000));
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> productService.changePrice(uuid, product))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품 가격 변경 시, 상품 가격이 0원 미만인 경우 변경을 실패한다")
    @Test
    void changePrice_price_exception() {
        //given
        Product product = 상품_생성("피자", BigDecimal.valueOf(-1));

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
    }

    @DisplayName("상품 목록을 조회한다")
    @Test
    void getAll() {
        //given
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<Product> products = productService.findAll();

        //then
        assertThat(products).hasSize(0);
        then(productRepository).should(times(1)).findAll();
    }
}
