package kitchenpos.application;

import static kitchenpos.test.constant.MethodSource.EMPTY_LIST;
import static kitchenpos.test.constant.MethodSource.NEGATIVE_NUMBERS;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.test.Fixture;
import kitchenpos.test.UnitTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("메뉴")
class MenuServiceTest extends UnitTestCase {

    @InjectMocks
    private MenuService service;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @DisplayName("등록")
    @Nested
    class CreateTest {

        private MenuGroup menuGroup;
        private Menu requestMenu;
        private Product product;

        @BeforeEach
        void setUp() {
            requestMenu = Fixture.createMenu();
            product = Fixture.createProduct();
            menuGroup = Fixture.createMenuGroup();
        }

        @DisplayName("메뉴 그룹, 제품 목록, 이름, 가격, 노출 여부로 등록한다.")
        @Test
        void success() {
            // given
            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given((productRepository.findById(any())))
                    .willReturn(Optional.of(product));

            // when then
            assertThatCode(() -> service.create(requestMenu))
                    .doesNotThrowAnyException();
        }

        @DisplayName("메뉴 그룹을 선택한다.")
        @Nested
        class MenuGroupTest {

            @DisplayName("메뉴 그룹을 선택하지 않으면 등록되지 않는다.")
            @Test
            void error1() {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.empty());

                // when
                assertThatThrownBy(() -> service.create(requestMenu))
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @DisplayName("제품 목록을 선택한다.")
        @Nested
        class MenuProductTest {

            @DisplayName("제품 목록을 선택하지 않으면 등록되지 않는다.")
            @ParameterizedTest
            @NullSource
            @MethodSource(EMPTY_LIST)
            void error1(List<MenuProduct> actual) {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.of(menuGroup));

                // when
                requestMenu.setMenuProducts(actual);

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }

            @DisplayName("메뉴에 등록할 제품의 수량은 0개 이상이어야 한다.")
            @ParameterizedTest
            @MethodSource(NEGATIVE_NUMBERS)
            void error2(BigDecimal actual) {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.of(menuGroup));
                given(productRepository.findAllByIdIn(any()))
                        .willReturn(List.of(product));

                // when
                int quantityOfMenuProduct = actual.intValue();
                Menu requestMenu = Fixture.createMenu(quantityOfMenuProduct);

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }

            @DisplayName("선택한 제품 목록의 제품 중 존재하지 않는 제품이 있다면 등록되지 않는다.")
            @Test
            void error3() {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.of(menuGroup));
                given(productRepository.findAllByIdIn(any()))
                        .willReturn(List.of(product, product));

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }
        }

        @DisplayName("메뉴 가격을 입력한다.")
        @Nested
        class PriceTest {

            @DisplayName("가격은 0원 이상 입력 가능하다.")
            @ParameterizedTest
            @NullSource
            @MethodSource(NEGATIVE_NUMBERS)
            void error1(BigDecimal actual) {
                // given
                requestMenu.setPrice(actual);

                // when then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }

            @DisplayName("가격은 제품 목록의 제품의 가격의 합보다 초과할 수 없다.")
            @Test
            void error2() {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.of(menuGroup));
                given(productRepository.findAllByIdIn(any()))
                        .willReturn(List.of(product));
                given((productRepository.findById(any())))
                        .willReturn(Optional.of(product));

                // when
                BigDecimal priceForAllProducts = Fixture.PRICES_FOR_ALL_PRODUCTS_ON_THE_MENU;
                requestMenu.setPrice(priceForAllProducts.add(BigDecimal.ONE));

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }
        }

        @DisplayName("메뉴 이름을 입력한다.")
        @Nested
        class NameTest {

            @DisplayName("메뉴 이름은 비어 있을 수 없다.")
            @ParameterizedTest
            @NullSource
            void error1(String actual) {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.of(menuGroup));
                given(productRepository.findAllByIdIn(any()))
                        .willReturn(List.of(product));

                given((productRepository.findById(any())))
                        .willReturn(Optional.of(product));

                // when
                requestMenu.setName(actual);

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }

            @DisplayName("이름은 비속어를 포함할 수 없다.")
            @Test
            void error2() {
                // given
                given(menuGroupRepository.findById(any()))
                        .willReturn(Optional.of(menuGroup));
                given(productRepository.findAllByIdIn(any()))
                        .willReturn(List.of(product));

                given((productRepository.findById(any())))
                        .willReturn(Optional.of(product));

                // when
                when(purgomalumClient.containsProfanity(any()))
                        .thenReturn(Boolean.TRUE);

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(requestMenu));
            }
        }
    }

    @DisplayName("가격 수정")
    @Nested
    class ChangePriceTest {

        private UUID menuId;
        private Menu requestMenu;

        @BeforeEach
        void setUp() {
            menuId = UUID.randomUUID();
            requestMenu = new Menu();
            requestMenu.setPrice(BigDecimal.ONE);
        }

        @DisplayName("등록된 메뉴의 가격을 수정할 수 있다.")
        @Test
        void success() {
            // when
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(Fixture.createMenu()));

            // then
            assertThatCode(() -> service.changePrice(menuId, requestMenu))
                    .doesNotThrowAnyException();
        }

        @DisplayName("가격이 0원 이상이어야 한다.")
        @ParameterizedTest
        @NullSource
        @MethodSource(NEGATIVE_NUMBERS)
        void error1(BigDecimal actual) {
            // given
            requestMenu.setPrice(actual);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.changePrice(menuId, requestMenu));
        }

        @DisplayName("메뉴의 가격이 갖고 있는 상품 목록 가격의 합보다 크면 수정되지 않는다.")
        @Test
        void error2() {
            // given
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(Fixture.createMenu()));

            // when
            BigDecimal priceForAllProducts = Fixture.PRICES_FOR_ALL_PRODUCTS_ON_THE_MENU;
            requestMenu.setPrice(priceForAllProducts.add(BigDecimal.ONE));

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.changePrice(menuId, requestMenu));
        }
    }

    @DisplayName("활성화")
    @Nested
    class DisplayTest {

        private UUID menuId;
        private Menu menu;

        @BeforeEach
        void setUp() {
            menu = Fixture.createMenu();
            menuId = menu.getId();
        }

        @DisplayName("메뉴를 활성화하여 노출할 수 한다.")
        @Test
        void success() {
            // given
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when then
            assertThat(service.display(menuId))
                    .hasFieldOrPropertyWithValue("displayed", Boolean.TRUE);
        }

        @DisplayName("메뉴의 가격이 갖고 있는 상품 목록 가격의 합보다 크면 수정되지 않는다.")
        @Test
        void error1() {
            // given
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when
            BigDecimal priceForAllProducts = Fixture.PRICES_FOR_ALL_PRODUCTS_ON_THE_MENU;
            menu.setPrice(priceForAllProducts.add(BigDecimal.ONE));

            // then
            assertThatThrownBy(() -> service.display(menuId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("비활성화")
    @Nested
    class HideTest {

        private UUID menuId;

        @BeforeEach
        void setUp() {
            menuId = UUID.randomUUID();
        }

        @Test
        void success() {
            // given
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(Fixture.createMenu()));

            // when then
            assertThat(service.hide(menuId))
                    .hasFieldOrPropertyWithValue("displayed", Boolean.FALSE);
        }

        @DisplayName("삭제된 메뉴는 비활성화 할 수 없다.")
        @Test
        void error() {
            assertThatThrownBy(() -> service.hide(menuId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("등록된 메뉴를 조회할 수 있다.")
    @Test
    void findAll() {
        assertThatCode(() -> service.findAll())
                .doesNotThrowAnyException();
    }
}
