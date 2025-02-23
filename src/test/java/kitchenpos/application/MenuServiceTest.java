package kitchenpos.application;

import config.UnitTest;
import helper.PriceGenerator;
import kitchenpos.domain.*;
import kitchenpos.infra.*;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@UnitTest
@DisplayName("메뉴 서비스 테스트")
class MenuServiceTest {

    private MenuRepository menuRepository;

    private MenuGroupRepository menuGroupRepository;

    private ProductRepository productRepository;

    private ProfanityChecker profanityChecker;

    private MenuService sut;

    @BeforeEach
    void setUp() {
        menuRepository = new InmemoryMenuRepository();
        menuGroupRepository = new InmemoryMenuGroupRepository();
        productRepository = new InmemoryProductRepository();
        profanityChecker = new FakeProfanityChecker();
        sut = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityChecker);
    }


    @Nested
    @DisplayName("메뉴 생성")
    class CreateMenuCases {
        @Test
        @DisplayName("성공: 유효한 입력 값으로 메뉴가 생성된다.")
        void createMenuSuccess() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());
            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .build();

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
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());

            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .price(null)
                    .build();

            // when & then
            assertThrows(MenuPriceException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 요청 메뉴 가격이 0원 미만이면 MenuPriceException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void createMenuWithNegativePriceThrowIllegalArgumentException(long price) {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());

            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .price(PriceGenerator.of(price))
                    .build();

            // when & then
            assertThrows(MenuPriceException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 요청 메뉴 그룹이 없으면 MenuGroupNotFoundException 발생")
        void createMenuWithoutMenuGroupThrowNoSuchElementException() {
            // given
            Menu request = aMenuRequest()
                    .menuGroup(aMenuGroupRequest().build())
                    .build();

            // when & then
            assertThrows(MenuGroupNotFoundException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 요청 메뉴 구성 상품이 하나도 없으면 MenuProductsNullOrEmptyException 발생")
        void createMenuWithoutMenuProductsThrowIllegalArgumentException() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            // 구성 상품이 없는 메뉴 생성
            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(null)
                    .build();

            // when & then
            assertThrows(MenuProductsNullOrEmptyException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 요청된 일부 메뉴 구성 상품 중, 실제 존재하지 않는 상품이 있으면 MenuProductsNotMatchedException 발생")
        void createMenuWithNonexistentMenuProductThrowIllegalArgumentException() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product existProduct = productRepository.save(aProductRequest().name("존재하는 상품").build());
            Product nonExistProduct = aProductRequest().name("존재하지 않는 상품").build();

            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(
                            List.of(aMenuProductRequest(existProduct).build()
                                    , aMenuProductRequest(nonExistProduct).build())
                    )
                    .build();

            // when & then
            assertThrows(MenuProductsNotMatchedException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 메뉴 구성 상품의 수량이 음수이면 MenuProductNegativeQuantityException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void createMenuWithNegativeMenuProductQuantityThrowIllegalArgumentException(long negativeQuantity) {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());
            // 음수 수량을 가진 메뉴 구성 상품이 포함된 메뉴 생성
            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(
                            List.of(aMenuProductRequest(product).quantity(negativeQuantity).build())
                    )
                    .build();

            // when & then
            assertThrows(MenuProductNegativeQuantityException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 메뉴의 가격이 구성 상품 가격 총합보다 높으면 MenuPriceHigherThanProductPriceSumException 발생")
        @ValueSource(longs = {30000, 50000, 80000})
        void createMenuWithPriceHigherThanSumOfMenuProductPricesThrowIllegalArgumentException(long higherThanMenuProductPrice) {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().price(PriceGenerator.of(10000L)).build());
            Product anotherProduct = productRepository.save(aProductRequest().price(PriceGenerator.of(5000L)).build());

            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(
                            List.of(aMenuProductRequest(product).build()
                                    , aMenuProductRequest(anotherProduct).build())
                    )
                    .price(PriceGenerator.of(higherThanMenuProductPrice))
                    .build();

            // when & then
            assertThrows(MenuPriceHigherThanProductPriceSumException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 메뉴명이 없으면 MenuNameException 발생")
        void createMenuWithoutNameThrowIllegalArgumentException() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());

            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .name(null)
                    .build();

            // when & then
            assertThrows(MenuNameException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 메뉴에 비속어가 포함되면 MenuNameException 발생")
        @ValueSource(strings = {"욕설1", "욕설2", "비속어1", "비속어2"})
        void createMenuWithEmptyOrProfanityNameThrowIllegalArgumentException(String profanity) {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());

            Menu request = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .name(profanity)
                    .build();

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
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());
            BigDecimal menuPrice = new BigDecimal(10000);
            Menu savedMenu = menuRepository.save(aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .price(menuPrice)
                    .build());

            Menu request = aMenuRequest()
                    .id(savedMenu.getId())
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()))
                    .price(PriceGenerator.of(price))
                    .build();

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
            Menu savedMenu = menuRepository.save(aMenuRequest().build());
            Menu request = aMenuRequest()
                    .id(savedMenu.getId())
                    .price(null)
                    .build();

            // when & then
            assertThrows(MenuChangePriceException.class, () -> sut.changePrice(savedMenu.getId(), request));
        }

        @ParameterizedTest
        @DisplayName("실패: 변경할 메뉴가격이 음수이면 MenuChangePriceException 발생")
        @ValueSource(longs = {-1, -1000, -10000})
        void changeMenuPriceNegativePriceThrowIllegalArgumentException(long price) {
            // given
            Menu savedMenu = menuRepository.save(aMenuRequest().build());
            Menu request = aMenuRequest()
                    .id(savedMenu.getId())
                    .price(PriceGenerator.of(price))
                    .build();

            // when & then
            assertThrows(MenuChangePriceException.class, () -> sut.changePrice(savedMenu.getId(), request));
        }

        @Test
        @DisplayName("실패: 변경할 메뉴가 존재하지 않으면 MenuNotFoundException 발생")
        void changeMenuPriceWithNonexistentMenuThrowNoSuchElementException() {
            // given
            Menu nonExistProduct = aMenuRequest().build();
            Menu request = aMenuRequest()
                    .id(nonExistProduct.getId())
                    .price(PriceGenerator.of(10000)).build();

            // when & then
            assertThatThrownBy(() -> sut.changePrice(nonExistProduct.getId(), request))
                    .isInstanceOf(MenuNotFoundException.class);
        }

        @ParameterizedTest
        @DisplayName("실패: 변경할 메뉴 가격이 구성 상품 가격 총합보다 높으면 MenuPriceHigherThanProductPriceSumException 발생")
        @ValueSource(longs = {30000, 50000, 80000})
        void changeMenuPriceHigherThanMenuProductTotalThrowIllegalArgumentException(long bigPrice) {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().price(PriceGenerator.of(10000L)).build());
            Product anotherProduct = productRepository.save(aProductRequest().price(PriceGenerator.of(5000L)).build());

            Menu savedMenu = menuRepository.save(aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(aMenuProductRequest(product).build()
                            , aMenuProductRequest(anotherProduct).build()))
                    .price(PriceGenerator.of(15000L))
                    .build());
            Menu request = aMenuRequest()
                    .id(savedMenu.getId())
                    .menuGroup(menuGroup)
                    .price(PriceGenerator.of(bigPrice))
                    .build();

            // when & then
            assertThrows(MenuPriceHigherThanProductPriceSumException.class, () -> sut.changePrice(savedMenu.getId(), request));
        }
    }

    @Nested
    @DisplayName("메뉴 표시")
    class DisplayMenuCases {

        @Test
        @DisplayName("성공: 메뉴 가격이 구성 상품 총합과 같으면 표시 가능")
        void displayMenuSuccess() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
            Product product = productRepository.save(aProductRequest().build());
            Product anotherProduct = productRepository.save(aProductRequest().build());

            // Create a menu whose price equals the total price of its products
            Menu menuWithTotalPrice = aMenuRequest()
                    .menuGroup(menuGroup)
                    .menuProducts(List.of(
                            aMenuProductRequest(product).build(),
                            aMenuProductRequest(anotherProduct).build()))
                    .price(product.getPrice().add(anotherProduct.getPrice()))
                    .build();
            menuWithTotalPrice.setDisplayed(false); // initially hidden
            Menu savedMenu = menuRepository.save(menuWithTotalPrice);
            assertThat(savedMenu.isDisplayed()).isFalse();

            // when
            Menu result = sut.display(menuWithTotalPrice.getId());

            // then
            assertThat(result.isDisplayed()).isTrue();
        }
    }

    @Test
    @DisplayName("실패: 메뉴 가격이 구성 상품 가격 총합보다 크면 MenuPriceHigherThanProductPriceSumException 발생")
    void displayMenuFailWithHigherPrice() {
        // given
        MenuGroup menuGroup = menuGroupRepository.save(aMenuGroupRequest().build());
        Product product = productRepository.save(aProductRequest().price(PriceGenerator.of(15000)).build());
        Product anotherProduct = productRepository.save(aProductRequest().price(PriceGenerator.of(5000)).build());
        Menu request = menuRepository.save(aMenuRequest()
                .menuGroup(menuGroup)
                .menuProducts(List.of(aMenuProductRequest(product).build()
                        , aMenuProductRequest(anotherProduct).build()))
                .price(PriceGenerator.of(30000))
                .build());
        // when & then
        assertThrows(MenuPriceHigherThanProductPriceSumException.class, () -> sut.display(request.getId()));
    }

    @Test
    @DisplayName("메뉴 표시 - 실패: 존재하지 않는 메뉴 ID로 요청 시 MenuNotFoundException 발생")
    void displayMenuFailWithNonexistentMenu() {
        // given
        Menu request = aMenuRequest().build();
        // when & then
        assertThrows(MenuNotFoundException.class, () -> sut.display(request.getId()));
    }

    @Nested
    @DisplayName("메뉴 숨김")
    class HideMenuCases {

        @Test
        @DisplayName("성공: 메뉴 숨김 처리 성공")
        void hideMenuSuccess() {
            // given
            Menu request = menuRepository.save(aMenuRequest()
                    .displayed(true)
                    .build());
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
            Menu request = aMenuRequest().build();
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
            List<Menu> menus = List.of(
                    aMenuRequest().name("후라이드 치킨").build(),
                    aMenuRequest().name("양념 치킨").build()
            );
            menus.forEach(menuRepository::save);

            // when
            List<Menu> result = sut.findAll();

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(menus.size())
            );
        }
    }
}

