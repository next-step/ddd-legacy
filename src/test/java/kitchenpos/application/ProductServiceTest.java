package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuFixture;
import kitchenpos.MenuGroupFixture;
import kitchenpos.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@UnitTest
@DisplayName("상품 서비스 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService sut;

    public static final BigDecimal 음수_가격 = new BigDecimal(-1);

    @Nested
    @DisplayName("상품 생성")
    class CreateProductCases {

        @Test
        @DisplayName("상품 생성 - 성공: 유효한 상품이 생성된다.")
        void createProductSuccess() {
            // given
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품();

            given(productRepository.save(any(Product.class))).willReturn(후라이드_치킨_상품);
            given(purgomalumClient.containsProfanity(any(String.class))).willReturn(false);

            // when
            Product 결과 = sut.create(후라이드_치킨_상품);

            // then
            assertThat(결과)
                    .isNotNull()
                    .extracting(Product::getId, Product::getName, Product::getPrice)
                    .containsExactly(후라이드_치킨_상품.getId(), 후라이드_치킨_상품.getName(), 후라이드_치킨_상품.getPrice());

            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("상품 생성 - 실패: 가격이 음수이면 IllegalArgumentException 발생")
        void createProductFailWhenPriceIsNegative() {
            // given
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품(음수_가격);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(후라이드_치킨_상품));
        }

        @Test
        @DisplayName("상품 생성 - 실패: 가격이 null이면 IllegalArgumentException 발생")
        void createProductFailWhenPriceIsNull() {
            // given
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(후라이드_치킨_상품));
        }

        @Test
        @DisplayName("상품 생성 - 실패: 상품명이 null이면 IllegalArgumentException 발생")
        void createProductFailWhenNameIsNull() {
            // given
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품();
            후라이드_치킨_상품.setName(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(후라이드_치킨_상품));
        }

        @Test
        @DisplayName("상품 생성 - 실패: 상품명에 비속어가 포함되어 있으면 IllegalArgumentException 발생")
        void createProductFailWhenNameContainsProfanity() {
            // given
            Product 후라이드_치킨_상품 = ProductFixture.후라이드_치킨_상품();

            given(purgomalumClient.containsProfanity(any(String.class))).willReturn(true);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(후라이드_치킨_상품));
        }
    }

    @Nested
    @DisplayName("상품 가격 변경")
    class ChangePriceCases {

        @Test
        @DisplayName("상품 가격 변경 - 성공: 유효한 가격으로 변경할 수 있다.")
        void changePriceSuccess() {
            // given
            BigDecimal 변경할_가격 = new BigDecimal(20000);
            Product 기존_상품 = ProductFixture.후라이드_치킨_상품();
            Product 가격_변경_상품 = ProductFixture.가격만_변경된_상품(기존_상품, 변경할_가격);

            given(productRepository.findById(기존_상품.getId())).willReturn(Optional.of(기존_상품));
            given(menuRepository.findAllByProductId(기존_상품.getId())).willReturn(Collections.emptyList());

            // when
            Product 결과 = sut.changePrice(기존_상품.getId(), 가격_변경_상품);

            // then
            assertThat(결과.getPrice()).isEqualTo(변경할_가격);
            verify(productRepository).findById(기존_상품.getId());
            verify(menuRepository).findAllByProductId(기존_상품.getId());
        }

        @Test
        @DisplayName("상품 가격 변경 - 실패: 변경 가격이 음수이면 IllegalArgumentException 발생")
        void changePriceFailWhenNegative() {
            // given
            Product 기존_상품 = ProductFixture.후라이드_치킨_상품();
            Product 변경된_상품 = ProductFixture.후라이드_치킨_상품(음수_가격);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(기존_상품.getId(), 변경된_상품));
        }

        @Test
        @DisplayName("상품 가격 변경 - 실패: 변경 가격이 null이면 IllegalArgumentException 발생")
        void changePriceFailWhenNull() {
            // given
            Product 기존_상품 = ProductFixture.후라이드_치킨_상품();
            Product 변경된_상품 = ProductFixture.후라이드_치킨_상품(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(기존_상품.getId(), 변경된_상품));
        }

        @Test
        @DisplayName("상품 가격 변경 - 실패: 존재하지 않는 상품 ID일 경우 NoSuchElementException 발생")
        void changePriceFailWhenProductNotFound() {
            // given
            UUID 상품_ID = UUID.randomUUID();
            Product 변경된_상품 = ProductFixture.후라이드_치킨_상품();

            given(productRepository.findById(상품_ID)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.changePrice(상품_ID, 변경된_상품));
        }

        @Test
        @DisplayName("상품 가격 변경 - 특수 케이스: 가격 변경 후 메뉴 가격이 더 크면 메뉴가 숨김 처리된다.")
        void changePriceHidesMenuWhenInvalid() {
            // given
            BigDecimal 변경_가격 = new BigDecimal(10000);
            MenuGroup 추천_메뉴그룹 = MenuGroupFixture.추천_메뉴그룹();
            Product 기존_상품 = ProductFixture.후라이드_치킨_상품();
            Product 변경된_상품 = ProductFixture.후라이드_치킨_상품(변경_가격);

            Menu 기존_메뉴 = MenuFixture.후라이드_치킨_메뉴(추천_메뉴그룹, 기존_상품);
            기존_메뉴.setPrice(BigDecimal.valueOf(20000)); // 메뉴 가격이 상품 가격 합보다 높게 설정됨
            기존_메뉴.setDisplayed(true);

            given(productRepository.findById(기존_상품.getId())).willReturn(Optional.of(기존_상품));
            given(menuRepository.findAllByProductId(기존_상품.getId())).willReturn(List.of(기존_메뉴));

            // when
            sut.changePrice(기존_상품.getId(), 변경된_상품);

            // then
            assertThat(기존_메뉴.isDisplayed()).isFalse(); // 메뉴가 숨김 처리됨
            verify(menuRepository).findAllByProductId(기존_상품.getId());
        }
    }
}