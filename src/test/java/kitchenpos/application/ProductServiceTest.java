package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);

    }

    //이거나 0보다 작으면
    @DisplayName("상품 등록할때, 가격이 null이면 오류가 발생한다.")
    @ParameterizedTest
    @NullSource
    void priceIsNull(BigDecimal price) {
        Product product = new Product(UUID.randomUUID(), "김밥", price);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 등록할때, 가격이 0보다 작으면 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1, -99, -100000})
    void priceUnderZero(long price) {
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(price));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 등록할때, 이름이 null 이면 오류가 발생한다.")
    @Test
    void nameNull() {
        Product product = new Product(UUID.randomUUID(), null, BigDecimal.valueOf(12000));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 등록할때, 이름에 비속어가 포함되어 있다면 오류가 발생한다.")
    @Test
    void purgomalTrue() {
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);
        //when
        //then
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(12000));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품 등록할때, 가격이 0보다 크고 이름에 비속어가 없으면 정상 등록")
    @Test
    void normal() {
        //given
        Product 돈가스 = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(12000));
        given(productRepository.save(any())).willReturn(돈가스);

        //when
        Product input = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(12000));
        Product output = productService.create(input);

        //then
        assertThat(output).isEqualTo(돈가스);
    }

    @DisplayName("상품 수정할떄 상품 가격이 null이면 오류가 발생한다.")
    @ParameterizedTest
    @NullSource
    void changePrice_priceIsNull(BigDecimal price) {
        Product product = new Product(UUID.randomUUID(), "김밥", price);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
    }

    @DisplayName("상품 수정할떄 상품 가격이 0보다 작으면 오류가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1, -99, -100000})
    void updatePrice_priceUnderZero(long price) {
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(price));
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
    }

    @DisplayName("등록되지 않은 상품의 가격을 수정할때, 오류가 발생한다.")
    @Test
    void notFoundProduct() {
        //given
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        //when
        //then
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(45000));
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
    }

    @DisplayName("메뉴가 등록된 상품을 수정할때")
    @Test
    void isExistsMenuCase1() {
        //given
        Product 돈가스 = new Product(UUID.randomUUID(), "돈가스", BigDecimal.valueOf(12000));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(돈가스));
        given(menuRepository.findAllByProductId(any())).willReturn(List.of(new Menu()));

        //when
        //then
        Product product = new Product(UUID.randomUUID(), "김밥", BigDecimal.valueOf(45000));
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
    }
}