package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final ProductService productService = new ProductService(productRepository, menuRepository, purgomalumClient);

    @DisplayName("상품을 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"짜장면:7000", "우동:6000"}, delimiter = ':')
    void create(String name, BigDecimal price) {
        // given
        Product product = makeTestProduct(name, price);
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Product result = productService.create(product);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPrice()).isEqualTo(price);
    }


    @DisplayName("상품 가격은 0원보다 작다면 에러를 발생시킨다.")
    @ParameterizedTest
    @CsvSource(value = {"짜장면:-1", "우동:-3000"}, delimiter = ':')
    void minusPrice(String name, BigDecimal price) {
        // given
        Product product = makeTestProduct(name, price);

        // when
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // then
        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명은 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullSource
    void nullName(String name) {
        // given
        Product product = makeTestProduct(name, BigDecimal.ONE);

        // when
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // then
        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명에 비속어가 들어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @CsvSource(value = {"fuck:7000", "shit:6000"}, delimiter = ':')
    void hasProfanity(String name, BigDecimal price) {
        // given
        Product product = makeTestProduct(name, price);

        // when
        // Mock 객체가 containsProfanity() 호출 시 true를 반환하도록 설정
        when(purgomalumClient.containsProfanity(name)).thenReturn(true);
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // then
        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격이 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullSource
    void nullPrice(BigDecimal price) {
        // given
        Product product = makeTestProduct("짜장면", price);

        // when
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // then
        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격은 변경이 가능하다.")
    @ParameterizedTest
    @CsvSource(value = {"짜장면:7000:8000", "우동:6000:7000"}, delimiter = ':')
    void changePrice(String name, BigDecimal price, BigDecimal changePrice) {
        // given
        Product asProduct = makeTestProduct(name, price);
        Product toProduct = makeTestProduct(name, changePrice);

        // when
        when(productRepository.findById(any())).thenReturn(Optional.of(asProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of());

        Product product = productService.changePrice(asProduct.getId(), toProduct);

        // then
        assertThat(product.getPrice()).isEqualTo(toProduct.getPrice());
    }

    @DisplayName("메뉴에 있는 상품들의 가격의 총합이 메뉴의 전체 가격보다 낮으면 메뉴를 노출하지 않는다.")
    @ValueSource(longs = {9900L, 4000L})
    @ParameterizedTest
    void compareMenuPrice(Long price) {
        // given
        Product firstProduct = makeTestProduct("짜장면", BigDecimal.valueOf(price));
        MenuProduct firstMenuProduct = makeTestMenuProduct(firstProduct);
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(10000), makeTestMenuGroup("추천메뉴"), firstMenuProduct);

        // when
        when(productRepository.findById(any())).thenReturn(Optional.of(firstProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

        productService.changePrice(firstProduct.getId(), firstProduct);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }
}
