package kitchenpos.application;

import kitchenpos.MenuFixture;
import kitchenpos.MenuGroupFixture;
import kitchenpos.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.InmemoryMenuRepository;
import kitchenpos.infra.InmemoryProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@DisplayName("상품 서비스 테스트")
class ProductServiceTest {

    private ProductRepository productRepository;

    private MenuRepository menuRepository;

    private PurgomalumClient purgomalumClient;

    private ProductService sut;

    public ProductServiceTest() {
        this.productRepository = new InmemoryProductRepository();
        this.menuRepository = new InmemoryMenuRepository();
        this.purgomalumClient = new FakePurgomalumClient();
        this.sut = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("상품 생성")
    class CreateProductCases {

        @Test
        @DisplayName("성공: 유효한 상품이 생성된다.")
        void createProductSuccess() {
            // given
            Product request = ProductFixture.후라이드_치킨_상품_Request();

            // when
            Product result = sut.create(request);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo(request.getName()),
                    () -> assertThat(result.getPrice()).isEqualTo(request.getPrice())
            );
        }

        @ParameterizedTest
        @DisplayName("실패: 가격이 음수이면 IllegalArgumentException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void createProductFailWhenPriceIsNegative(long price) {
            // given
            Product request = ProductFixture.후라이드_치킨_상품_Request(BigDecimal.valueOf(price));

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 가격이 null이면 IllegalArgumentException 발생")
        void createProductFailWhenPriceIsNull() {
            // given
            Product request = ProductFixture.후라이드_치킨_상품_Request(null);

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 상품명이 null이면 IllegalArgumentException 발생")
        void createProductFailWhenNameIsNull() {
            // given
            Product request = ProductFixture.후라이드_치킨_상품_Request();
            request.setName(null);

            // when & then
            assertThrows(ProductNameException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 상품명에 비속어가 포함되어 있으면 IllegalArgumentException 발생")
        @ValueSource(strings = {"욕설1", "욕설2", "비속어1", "비속어2"})
        void createProductFailWhenNameContainsProfanity(String profanity) {
            // given
            Product request = ProductFixture.후라이드_치킨_상품_Request();
            request.setName(profanity);

            // when & then
            assertThrows(ProductNameException.class, () -> sut.create(request));
        }
    }

    @Nested
    @DisplayName("상품 가격 변경")
    class ChangePriceCases {

        @ParameterizedTest
        @DisplayName("성공: 유효한 가격으로 변경할 수 있다.")
        @ValueSource(longs = {1000, 10000, 100000})
        void changePriceSuccess(long price) {
            // given
            BigDecimal changedPrice = BigDecimal.valueOf(price);
            Product saved = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product request = ProductFixture.가격만_변경된_상품(saved, changedPrice);

            // when
            Product result = sut.changePrice(saved.getId(), request);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(saved.getId()),
                    () -> assertThat(result.getPrice()).isEqualTo(changedPrice)
            );
        }

        @ParameterizedTest
        @DisplayName("실패: 변경 가격이 음수이면 IllegalArgumentException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void changePriceFailWhenNegative(long price) {
            // given
            Product saved = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product request = ProductFixture.후라이드_치킨_상품_Request(BigDecimal.valueOf(price));

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.changePrice(saved.getId(), request));
        }

        @Test
        @DisplayName("실패: 변경 가격이 null이면 IllegalArgumentException 발생")
        void changePriceFailWhenNull() {
            // given
            Product saved = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product request = ProductFixture.후라이드_치킨_상품_Request(null);

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.changePrice(saved.getId(), request));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 상품 ID일 경우 NoSuchElementException 발생")
        void changePriceFailWhenProductNotFound() {
            // given
            Product request = ProductFixture.후라이드_치킨_상품_Request();


            // when & then
            assertThrows(ProductNotFoundException.class, () -> sut.changePrice(null, request));
        }

        @Test
        @DisplayName("특수 케이스: 가격 변경 후 메뉴 가격이 더 크면 메뉴가 숨김 처리된다.")
        void changePriceHidesMenuWhenInvalid() {
            // given
            BigDecimal lowerPrice = new BigDecimal(10000);
            BigDecimal higherPrice = new BigDecimal(20000);

            MenuGroup recommendedMenuGroup = MenuGroupFixture.추천_메뉴그룹();
            Product savedProduct = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product requestProduct = ProductFixture.가격만_변경된_상품(savedProduct, lowerPrice);

            Menu menuBeforePriceChange = MenuFixture.후라이드_치킨_메뉴(recommendedMenuGroup, savedProduct);
            menuBeforePriceChange.setPrice(higherPrice); // 메뉴 가격이 상품 가격 합보다 높게 설정됨
            menuBeforePriceChange.setDisplayed(true);
            Menu savedMenu = menuRepository.save(menuBeforePriceChange);

            assertThat(savedMenu.isDisplayed()).isTrue(); // 변경 전, 메뉴는 노출 상태여야 함

            // when
            sut.changePrice(savedProduct.getId(), requestProduct);

            // then
            Menu updatedMenu = menuRepository.findById(savedMenu.getId()).orElseThrow();
            Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

            assertAll(
                    () -> assertThat(updatedProduct.getPrice()).isEqualTo(lowerPrice), //상품 가격이 변경됨
                    () -> assertThat(updatedMenu.isDisplayed()).isFalse() //메뉴가 숨김 처리됨
            );
        }
    }
}