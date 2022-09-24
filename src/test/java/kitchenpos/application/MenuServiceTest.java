package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import javax.validation.constraints.Null;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import kitchenpos.helper.InMemoryMenuGroupRepository;
import kitchenpos.helper.InMemoryMenuRepository;
import kitchenpos.helper.InMemoryProductRepository;
import kitchenpos.helper.InMemoryProfanityClient;
import kitchenpos.helper.MenuFixture;
import kitchenpos.helper.MenuGroupFixture;
import kitchenpos.helper.MenuProductFixture;
import kitchenpos.helper.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuServiceTest {

    private static final String PROVIDE_NEGATIVE_PRICE = "kitchenpos.application.MenuServiceTest#provideNegativePrice";

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final ProfanityClient profanityClient = new InMemoryProfanityClient();

    private MenuService testTarget;

    @BeforeEach
    void setUp() {
        testTarget = new MenuService(
            menuRepository,
            menuGroupRepository,
            productRepository,
            profanityClient
        );
    }

    @DisplayName("메뉴 등록 테스트")
    @Nested
    class CreateTest {

        @DisplayName("메뉴를 등록 할 수 있다.")
        @Test
        void test01() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            Product product = productRepository.save(ProductFixture.FRIED_CHICKEN);

            Menu request = MenuFixture.request(
                6000,
                menuGroup.getId(),
                "후라이드 치킨",
                true,
                MenuProductFixture.request(product.getId(), 1)
            );

            // when
            Menu actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(request.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(request.getPrice()),
                () -> assertThat(actual.getMenuGroup().getId()).isEqualTo(request.getMenuGroupId()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(request.isDisplayed()),
                () -> assertThat(actual.getMenuProducts())
                    .singleElement()
                    .matches(menuProduct -> menuProduct.getQuantity() == 1)
                    .extracting(MenuProduct::getProduct)
                    .extracting(Product::getId)
                    .matches(productId -> productId.equals(ProductFixture.FRIED_CHICKEN.getId()))
            );
        }

        @DisplayName("메뉴 가격은 0원 미만일 수 없다.")
        @Test
        void test02() {
            // given
            Menu request = MenuFixture.request(
                -1,
                MenuGroupFixture.CHICKEN.getId(),
                "후라이드 치킨",
                true,
                MenuProductFixture.request(ProductFixture.FRIED_CHICKEN.getId(), 1)
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴는 미리 등록된 메뉴 그룹에 속해야 한다.")
        @Test
        void test03() {
            // given
            Menu request = MenuFixture.request(
                6000,
                UUID.randomUUID(),
                "후라이드 치킨",
                true,
                MenuProductFixture.request(ProductFixture.FRIED_CHICKEN.getId(), 1)
            );

            // when & then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴는 하나 이상의 메뉴 상품을 가진다.")
        @Test
        void test04() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            Menu request = MenuFixture.request(
                6000,
                menuGroup.getId(),
                "후라이드 치킨",
                true
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴는 존재하지 않는 상품을 메뉴 상품으로 가질 수 없다.")
        @Test
        void test05() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            Menu request = MenuFixture.request(
                6000,
                menuGroup.getId(),
                "후라이드 치킨",
                true,
                MenuProductFixture.request(UUID.randomUUID(), 1)
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴 상품의 수량은 0 이상이다.")
        @Test
        void test06() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            Product product = productRepository.save(ProductFixture.FRIED_CHICKEN);
            Menu request = MenuFixture.request(
                6000,
                menuGroup.getId(),
                "후라이드 치킨",
                true,
                MenuProductFixture.request(product.getId(), 0)
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴 가격은 메뉴 상품 가격의 총합보다 클 수 없다.")
        @Test
        void test07() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            Product product = productRepository.save(ProductFixture.FRIED_CHICKEN);
            BigDecimal menuPrice = product.getPrice().add(BigDecimal.ONE);
            Menu request = MenuFixture.request(
                menuPrice.intValue(),
                menuGroup.getId(),
                "후라이드 치킨",
                true,
                MenuProductFixture.request(product.getId(), 1)
            );

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("메뉴 이름은 비어 있을 수 없고, 비속어를 포함 할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"욕설이 포함됨", "비속어가 포함됨"})
        @NullAndEmptySource
        void test08(String name) {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            Product product = productRepository.save(ProductFixture.FRIED_CHICKEN);

            Menu request = MenuFixture.request(
                6000,
                menuGroup.getId(),
                name,
                true,
                MenuProductFixture.request(product.getId(), 1)
            );

            // when
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }
    }

    @DisplayName("메뉴 가격 변경 테스트")
    @Nested
    class ChangePriceTest {

        @DisplayName("메뉴의 가격을 변경 할 수 있다.")
        @Test
        void test01() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Menu request = MenuFixture.request(BigDecimal.valueOf(5000));

            // when
            Menu actual = testTarget.changePrice(menu.getId(), request);

            // then
            assertThat(actual.getPrice()).isEqualTo(request.getPrice());
        }

        @DisplayName("메뉴 가격은 0원 이상이어야 한다.")
        @ParameterizedTest
        @MethodSource(PROVIDE_NEGATIVE_PRICE)
        @Null
        void test02(BigDecimal price) {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Menu request = MenuFixture.request(price);

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.changePrice(menu.getId(), request));
        }


        @DisplayName("존재하지 않는 메뉴의 가격을 변경 할 수 없다.")
        @Test
        void test03() {
            // given
            UUID menuId = UUID.randomUUID();
            Menu request = MenuFixture.request(BigDecimal.valueOf(5000));

            // when & then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> testTarget.changePrice(menuId, request));
        }

        @DisplayName("메뉴의 가격은 메뉴 상품 가격의 합보다 클 수 없다.")
        @Test
        void test04() {
            // given
            Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
            Menu request = MenuFixture.request(BigDecimal.valueOf(7000));

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.changePrice(menu.getId(), request));
        }
    }

    private static Stream<Arguments> provideNegativePrice() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(-1))
        );
    }
}