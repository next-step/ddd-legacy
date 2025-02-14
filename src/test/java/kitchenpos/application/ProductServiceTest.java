package kitchenpos.application;

import config.UnitTest;
import helper.PriceGenerator;
import kitchenpos.domain.*;
import kitchenpos.infra.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.MenuFixture.*;
import static kitchenpos.MenuGroupFixture.*;
import static kitchenpos.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@UnitTest
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
            Product request = aProductRequest()
                    .build();

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
        @DisplayName("실패: 가격이 음수이면 ProductPriceException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void createProductFailWhenPriceIsNegative(long price) {
            // given
            Product request = aProductRequest()
                    .price(PriceGenerator.of(price))
                    .build();

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.create(request));
        }


        @Test
        @DisplayName("실패: 가격이 null이면 ProductPriceException 발생")
        void createProductFailWhenPriceIsNull() {
            // given
            Product request = aProductRequest()
                    .price(null)
                    .build();

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.create(request));
        }


        @Test
        @DisplayName("실패: 상품명이 null이면 ProductNameException 발생")
        void createProductFailWhenNameIsNull() {
            // given
            Product request = aProductRequest()
                    .name(null)
                    .build();

            // when & then
            assertThrows(ProductNameException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 상품명에 비속어가 포함되어 있으면 ProductNameException 발생")
        @ValueSource(strings = {"욕설1", "욕설2", "비속어1", "비속어2"})
        void createProductFailWhenNameContainsProfanity(String profanity) {
            // given
            Product request = aProductRequest()
                    .name(profanity)
                    .build();

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
            BigDecimal changedPrice = PriceGenerator.of(price);
            Product savedProduct = productRepository.save(aProductRequest().build());
            Product request = aProductRequest()
                    .id(savedProduct.getId())
                    .price(changedPrice)
                    .build();

            // when
            Product result = sut.changePrice(savedProduct.getId(), request);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(savedProduct.getId()),
                    () -> assertThat(result.getPrice()).isEqualTo(changedPrice)
            );
        }

        @ParameterizedTest
        @DisplayName("실패: 변경 가격이 음수이면 ProductPriceException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void changePriceFailWhenNegative(long price) {
            // given
            BigDecimal changedPrice = PriceGenerator.of(price);
            Product savedProduct = productRepository.save(aProductRequest().build());
            Product request = aProductRequest()
                    .id(savedProduct.getId())
                    .price(changedPrice)
                    .build();

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.changePrice(savedProduct.getId(), request));
        }

        @Test
        @DisplayName("실패: 변경 가격이 null이면 ProductPriceException 발생")
        void changePriceFailWhenNull() {
            // given
            Product savedProduct = productRepository.save(aProductRequest().build());
            Product request = aProductRequest()
                    .id(savedProduct.getId())
                    .price(null)
                    .build();

            // when & then
            assertThrows(ProductPriceException.class, () -> sut.changePrice(savedProduct.getId(), request));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 상품 ID일 경우 ProductNotFoundException 발생")
        void changePriceFailWhenProductNotFound() {
            // given
            Product request = aProductRequest().build();


            // when & then
            assertThrows(ProductNotFoundException.class, () -> sut.changePrice(null, request));
        }

        @Test
        @DisplayName("특수 케이스: 가격 변경 후 메뉴 가격이 더 크면 메뉴가 숨김 처리된다.")
        void changePriceHidesMenuWhenInvalid() {
            // given
            long basePrice = 10000;
            BigDecimal lowerPrice = PriceGenerator.smallerThan(basePrice);
            BigDecimal higherPrice = PriceGenerator.biggerThan(basePrice);

            // Menu보다
            Product savedProduct = productRepository.save(aProductRequest().build());
            Product requestProduct = aProductRequest()
                    .id(savedProduct.getId())
                    .price(lowerPrice)
                    .build();

            // 기존 메뉴 생성: 추천 메뉴그룹에 속한 메뉴로, 메뉴 가격이 상품 가격 합보다 높게 설정됨
            MenuGroup menuGroup = aMenuGroupRequest().build();
            Menu menu = menuRepository.save(aMenuRequest()
                    .menuGroup(menuGroup)
                    .price(higherPrice)
                    .menuProducts(List.of(aMenuProductRequest(savedProduct).build()))
                    .displayed(true)
                    .build());

            assertThat(menu.isDisplayed()).isTrue(); // 변경 전 메뉴는 노출 상태

            // when
            sut.changePrice(savedProduct.getId(), requestProduct);

            // then
            Menu updatedMenu = menuRepository.findById(menu.getId()).orElseThrow();
            Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

            assertAll(
                    () -> assertThat(updatedProduct.getPrice()).isEqualTo(lowerPrice), // 상품 가격 변경 확인
                    () -> assertThat(updatedMenu.isDisplayed()).isFalse() // 메뉴 숨김 처리 확인
            );
        }
    }
}