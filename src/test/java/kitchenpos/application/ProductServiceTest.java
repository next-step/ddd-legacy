package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductFixture;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.MockPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.domain.MenuFixture.CHICKEN_MENU;
import static kitchenpos.domain.ProductFixture.HONEY_COMBO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Spy
    private PurgomalumClient purgomalumClient = new MockPurgomalumClient();

    @InjectMocks
    private ProductService sut;

    @Test
    @DisplayName("상품 가격이 비어있으면 상품 생성 실패")
    void createFail01() {
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(new Product()));
    }

    @Test
    @DisplayName("상품가격이 음수면 상품 생성 실패")
    void createFail02() {
        // given
        Product request = ProductFixture.builder()
                                        .price(BigDecimal.valueOf(-1))
                                        .build();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("상품명이 비어있으면 상품 생성 실패")
    void createFail03() {
        // given
        Product request = ProductFixture.builder()
                                        .price(BigDecimal.TEN)
                                        .build();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("상품명에 욕설이 사용되었으면 상품 생성 실패")
    void createFail04() {
        // given
        Product request = ProductFixture.builder()
                                        .name("f**k")
                                        .price(BigDecimal.TEN)
                                        .build();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("상품 생성 성공")
    void createSuccess() {
        // given
        Product request = ProductFixture.builder()
                                        .name("Product")
                                        .price(BigDecimal.TEN)
                                        .build();

        given(productRepository.save(any())).willReturn(request);

        // when
        Product actual = sut.create(request);

        // then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(request.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(request.getPrice())
        );
    }

    @Test
    @DisplayName("상품 가격이 비어있으면 상품 가격 변경 실패")
    void changePriceFail01() {
        // given
        Product request = ProductFixture.builder().build();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.changePrice(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("상품 가격이 음수면 상품 가격 변경 실패")
    void changePriceFail02() {
        // given
        Product request = ProductFixture.builder()
                                        .price(BigDecimal.valueOf(-1))
                                        .build();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.changePrice(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("상품 가격 변경 시 해당 상품이 포함된 메뉴의 가격이 메뉴에 포함된 상품 가격의 합보다 크거나 같으면 메뉴를 비전시 상태로 변경")
    void changePriceSuccess01() {
        // given
        Product request = ProductFixture.builder()
                                        .price(BigDecimal.valueOf(100))
                                        .build();

        given(productRepository.findById(any())).willReturn(Optional.of(HONEY_COMBO));
        given(menuRepository.findAllByProductId(any())).willReturn(Collections.singletonList(CHICKEN_MENU));

        // when
        Product actual = sut.changePrice(UUID.randomUUID(), request);

        // then
        assertThat(actual.getPrice()).isEqualByComparingTo(request.getPrice());
        assertThat(CHICKEN_MENU.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("상품 가격 변경 시 해당 상품이 포함된 메뉴의 가격이 메뉴에 포함된 상품 가격의 합보다 작으면 상품 가격만 변경")
    void changePriceSuccess02() {
        // given
        Product request = ProductFixture.builder()
                                        .price(BigDecimal.valueOf(25000))
                                        .build();

        given(productRepository.findById(any())).willReturn(Optional.of(HONEY_COMBO));
        given(menuRepository.findAllByProductId(any())).willReturn(Collections.singletonList(CHICKEN_MENU));

        // when
        Product actual = sut.changePrice(UUID.randomUUID(), request);

        // then
        assertThat(actual.getPrice()).isEqualByComparingTo(request.getPrice());
        assertThat(CHICKEN_MENU.isDisplayed()).isFalse();
    }
}
