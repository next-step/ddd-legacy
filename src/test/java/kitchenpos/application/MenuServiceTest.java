package kitchenpos.application;

import config.UnitTest;
import helper.PriceGenerator;
import kitchenpos.MenuFixture;
import kitchenpos.MenuGroupFixture;
import kitchenpos.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@UnitTest
@DisplayName("메뉴 서비스 테스트")
class MenuServiceTest {

    private MenuRepository menuRepository;

    private MenuGroupRepository menuGroupRepository;

    private ProductRepository productRepository;

    private PurgomalumClient purgomalumClient;

    private MenuService sut;

    @BeforeEach
    void setUp() {
        menuRepository = new InmemoryMenuRepository();
        menuGroupRepository = new InmemoryMenuGroupRepository();
        productRepository = new InmemoryProductRepository();
        purgomalumClient = new FakePurgomalumClient();
        sut = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }


    @Nested
    @DisplayName("메뉴 생성")
    class CreateMenuCases {
        @Test
        @DisplayName("성공: 유효한 입력 값으로 메뉴가 생성된다.")
        void createMenuSuccess() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);

            // when
            var result = sut.create(request);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo(request.getName()),
                    () -> assertThat(result.getPrice()).isEqualTo(request.getPrice())
            );
        }

        @Test
        @DisplayName("실패: 요청 메뉴 가격이 없으면 MenuPriceException 발생")
        void createMenuWithoutPriceThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());

            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            request.setPrice(null);

            // when & then
            assertThrows(MenuPriceException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 요청 메뉴 가격이 0원 미만이면 MenuPriceException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void createMenuWithNegativePriceThrowIllegalArgumentException(long price) {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());

            // 가격이 음수인 메뉴 생성
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            request.setPrice(PriceGenerator.of(price));

            // when & then
            assertThrows(MenuPriceException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 요청 메뉴 그룹이 없으면 MenuGroupNotFoundException 발생")
        void createMenuWithoutMenuGroupThrowNoSuchElementException() {
            // given
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request();

            // when & then
            assertThrows(MenuGroupNotFoundException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 요청 메뉴 구성 상품이 하나도 없으면 MenuProductsNullOrEmptyException 발생")
        void createMenuWithoutMenuProductsThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            // 구성 상품이 없는 메뉴 생성
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹);

            // when & then
            assertThrows(MenuProductsNullOrEmptyException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 요청된 일부 메뉴 구성 상품 중, 실제 존재하지 않는 상품이 있으면 MenuProductsNotMatchedException 발생")
        void createMenuWithNonexistentMenuProductThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 존재하는_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request()); // 존재하는 상품
            Product 존재하지_않는_상품 = ProductFixture.콜라_상품_Request(); // 존재하지 않는 상품

            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 존재하는_상품, 존재하지_않는_상품);

            // when & then
            assertThrows(MenuProductsNotMatchedException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 메뉴 구성 상품의 수량이 음수이면 MenuProductNegativeQuantityException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void createMenuWithNegativeMenuProductQuantityThrowIllegalArgumentException(long price) {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            MenuProduct 음수_구성상품 = new MenuProduct();
            음수_구성상품.setProduct(후라이드_치킨_상품);
            음수_구성상품.setProductId(후라이드_치킨_상품.getId());
            음수_구성상품.setQuantity(price);
            // 음수 수량을 가진 메뉴 구성 상품이 포함된 메뉴 생성
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품);
            request.setMenuProducts(List.of(음수_구성상품));

            // when & then
            assertThrows(MenuProductNegativeQuantityException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 메뉴의 가격이 구성 상품 가격 총합보다 높으면 MenuPriceHigherThanProductPriceSumException 발생")
        @ValueSource(longs = {30000, 50000, 80000})
        void createMenuWithPriceHigherThanSumOfMenuProductPricesThrowIllegalArgumentException(long price) {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request()); // 16,000원
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request()); // 1,000원

            // 총 상품 가격: 16,000 + 1,000 = 17,000원
            // 메뉴 가격을 17,000원보다 많게 설정하여 예외를 발생시킨다.
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            request.setPrice(PriceGenerator.of(price));

            // when & then
            assertThrows(MenuPriceHigherThanProductPriceSumException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 메뉴명이 없으면 MenuNameException 발생")
        void createMenuWithoutNameThrowIllegalArgumentException() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());

            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품);
            request.setName(null);

            // when & then
            assertThrows(MenuNameException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 메뉴에 비속어가 포함되면 MenuNameException 발생")
        @ValueSource(strings = {"욕설1", "욕설2", "비속어1", "비속어2"})
        void createMenuWithEmptyOrProfanityNameThrowIllegalArgumentException(String profanity) {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());

            Menu request = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품);
            request.setName(profanity);

            // when & then
            assertThrows(MenuNameException.class, () -> sut.create(request));
        }
    }


    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangeMenuPriceCases {

        @ParameterizedTest
        @DisplayName("성공: 기존 메뉴 가격이 변경된다.")
        @ValueSource(longs = {5000, 10000, 15000})
        void changeMenuPriceSuccess(long price) {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());

            Menu 기존_메뉴 = MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            Menu savedMenu = menuRepository.save(기존_메뉴);

            Menu request = MenuFixture.가격만_변경된_메뉴(기존_메뉴, PriceGenerator.of(price));// 기존 구성 상품 가격보다 낮거나 같아야 함

            // when
            Menu result = sut.changePrice(savedMenu.getId(), request);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(savedMenu.getId()),
                    () -> assertThat(result.getName()).isEqualTo(savedMenu.getName()),
                    () -> assertThat(result.getPrice()).isEqualTo(request.getPrice())
            );

        }

        @Test
        @DisplayName("실패: 변경할 가격이 없으면 MenuChangePriceException 발생")
        void changeMenuPriceWithoutPriceThrowIllegalArgumentException() {
            // given
            Menu 후라이드_치킨_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            Menu request = MenuFixture.가격만_변경된_메뉴(후라이드_치킨_메뉴, null);

            // when & then
            assertThrows(MenuChangePriceException.class, () -> sut.changePrice(후라이드_치킨_메뉴.getId(), request));
        }

        @ParameterizedTest
        @DisplayName("실패: 변경할 메뉴가격이 음수이면 MenuChangePriceException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void changeMenuPriceNegativePriceThrowIllegalArgumentException(long price) {
            // given
            Menu 후라이드_치킨_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            Menu request = MenuFixture.가격만_변경된_메뉴(후라이드_치킨_메뉴, PriceGenerator.of(price));

            // when & then
            assertThrows(MenuChangePriceException.class, () -> sut.changePrice(후라이드_치킨_메뉴.getId(), request));
        }

        @Test
        @DisplayName("실패: 변경할 메뉴가 존재하지 않으면 MenuNotFoundException 발생")
        void changeMenuPriceWithNonexistentMenuThrowNoSuchElementException() {
            // given
            Menu 존재하지_않는_메뉴 = MenuFixture.후라이드_치킨_메뉴_Request();
            Menu request = MenuFixture.가격만_변경된_메뉴(존재하지_않는_메뉴, PriceGenerator.of(15000));

            // when & then
            assertThatThrownBy(() -> sut.changePrice(존재하지_않는_메뉴.getId(), request))
                    .isInstanceOf(MenuNotFoundException.class);
        }

        @ParameterizedTest
        @DisplayName("실패: 변경할 메뉴 가격이 구성 상품 가격 총합보다 높으면 MenuPriceHigherThanProductPriceSumException 발생")
        @ValueSource(longs = {30000, 50000, 80000})
        void changeMenuPriceHigherThanMenuProductTotalThrowIllegalArgumentException(long bigPrice) {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());

            Menu 기존_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품));
            Menu request = MenuFixture.가격만_변경된_메뉴(기존_메뉴, PriceGenerator.of(bigPrice));

            // when & then
            assertThrows(MenuPriceHigherThanProductPriceSumException.class, () -> sut.changePrice(기존_메뉴.getId(), request));
        }
    }

    @Nested
    @DisplayName("메뉴 표시")
    class DisplayMenuCases {

        @Test
        @DisplayName("성공: 메뉴 가격이 구성 상품 총합과 같으면 표시 가능")
        void displayMenuSuccess() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());

            Menu 구성_상품_가격_총합과_동일한_메뉴 = MenuFixture.구성_상품_가격_총합과_동일한_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품);
            구성_상품_가격_총합과_동일한_메뉴.setDisplayed(false); // 기존에 숨겨진 상태
            Menu savedMenu = menuRepository.save(구성_상품_가격_총합과_동일한_메뉴);
            assertThat(savedMenu.isDisplayed()).isFalse();

            // when
            Menu result = sut.display(구성_상품_가격_총합과_동일한_메뉴.getId());

            // then
            assertThat(result.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("실패: 메뉴 가격이 구성 상품 가격 총합보다 크면 MenuPriceHigherThanProductPriceSumException 발생")
        void displayMenuFailWithHigherPrice() {
            // given
            MenuGroup 추천_메뉴그룹 = menuGroupRepository.save(MenuGroupFixture.추천_메뉴그룹_Request());
            Product 후라이드_치킨_상품 = productRepository.save(ProductFixture.후라이드_치킨_상품_Request());
            Product 콜라_상품 = productRepository.save(ProductFixture.콜라_상품_Request());
            Menu request= menuRepository.save(MenuFixture.구성_상품_가격_총합을_초과한_메뉴_Request(추천_메뉴그룹, 후라이드_치킨_상품, 콜라_상품));

            // when & then
            assertThrows(MenuPriceHigherThanProductPriceSumException.class, () -> sut.display(request.getId()));
        }

        @Test
        @DisplayName("메뉴 표시 - 실패: 존재하지 않는 메뉴 ID로 요청 시 MenuNotFoundException 발생")
        void displayMenuFailWithNonexistentMenu() {
            // given
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request();
            // when & then
            assertThrows(MenuNotFoundException.class, () -> sut.display(request.getId()));
        }
    }

    @Nested
    @DisplayName("메뉴 숨김")
    class HideMenuCases {

        @Test
        @DisplayName("성공: 메뉴 숨김 처리 성공")
        void hideMenuSuccess() {
            // given
            Menu 보임_메뉴_Request = MenuFixture.후라이드_치킨_메뉴_Request();
            보임_메뉴_Request.setDisplayed(true); // 기존에 표시된 상태
            Menu request = menuRepository.save(보임_메뉴_Request);
            assertThat(request.isDisplayed()).isTrue();
            // when
            Menu result = sut.hide(request.getId());

            // then
            assertThat(result.isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 메뉴 ID로 요청 시 MenuNotFoundException 발생")
        void hideMenuFailWithNonexistentMenu() {
            // given
            Menu request = MenuFixture.후라이드_치킨_메뉴_Request();
            // when & then
            assertThrows(MenuNotFoundException.class, () -> sut.hide(request.getId()));
        }
    }

    @Nested
    @DisplayName("메뉴 목록 조회")
    class ListMenusCases {

        @Test
        @DisplayName("성공: 메뉴 목록이 조회된다.")
        void listMenusSuccess() {
            // given
            Menu 후라이드_치킨_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            Menu 양념_치킨_메뉴 = menuRepository.save(MenuFixture.양념_치킨_메뉴_Request());

            // when
            List<Menu> result = sut.findAll();

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(2),
                    () -> assertThat(result).extracting("id").contains(후라이드_치킨_메뉴.getId(), 양념_치킨_메뉴.getId())
            );
        }
    }
}
