package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.TEST_MENU_GROUP;
import static kitchenpos.fixture.ProductFixture.CREATE_TEST_PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    @Mock
    private MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository = new InMemoryProductRepository();
    @Mock
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setup() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }


    @Nested
    @DisplayName("새로운 메뉴를 등록한다.")
    class createTestClass {

        @Test
        @DisplayName("새로운 메뉴를 정상적으로 등록한다.")
        void createTest() {
            // given
            MenuGroup menuGroup = TEST_MENU_GROUP();
            Product product = CREATE_TEST_PRODUCT();
            Menu menuRequest = CREATE_TEST_MENU(new BigDecimal(150), product);
            productRepository.save(product);
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(menuGroup));
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when
            Menu actual = menuService.create(menuRequest);

            // then
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getPrice()).isNotNull();
            assertThat(actual.getMenuProducts()).isNotNull();
            assertThat(actual.getMenuProducts()).extracting("product").containsExactly(product);
            assertThat(actual.getMenuProducts()).extracting("quantity").containsExactly(3L);
        }

        @ParameterizedTest
        @CsvSource(value = {"-50030", "-1", "null"})
        @DisplayName("메뉴의_가격은_0원_이상이여야_한다")
        void priceTest(String price) {
            // given && when
            BigDecimal value = price.equals("null") ? null : new BigDecimal(price);
            Menu menuRequest = CREATE_TEST_MENU(value);

            // then
            assertThatThrownBy(() -> menuService.create(menuRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("가격은 0이상이어야 합니다");
        }

        @Test
        @DisplayName("메뉴_생성시_상품을_등록해야한다")
        void productRequiredTest() {
            // given
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

            // when
            Menu menuRequest = CREATE_TEST_MENU((MenuProduct) null);

            //then
            assertThatThrownBy(() -> menuService.create(menuRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴의 구성품은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("메뉴_생성시_상품들은_등록된_상품이어야_한다")
        void productShouldExist() {
            // given
            Menu menuRequest = CREATE_TEST_MENU();
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

            // when && then
            assertThatThrownBy(() -> menuService.create(menuRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("구성품들은 기성 상품이어야 합니다");
        }

        @Test
        @DisplayName("메뉴의_상품들은_수량은_0개_이상이어야_한다")
        void quantityTest() {
            // given
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

            // when
            MenuProduct menuProduct = TEST_MENU_PRODUCT(-1);
            Menu menuRequest = CREATE_TEST_MENU(menuProduct);
            Product product = menuRequest.getMenuProducts().get(0).getProduct();
            productRepository.save(product);

            // then
            assertThatThrownBy(() -> menuService.create(menuRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴의 수량은 0이상이어야 합니다.");
        }

        @Test
        @DisplayName("메뉴의 가격은 포함된 상품들의 각 금액(상품 가격 X 수량)의 합보다 높을 수 없다.")
        void priceSumTest() {
            // given
            Product product = CREATE_TEST_PRODUCT();
            productRepository.save(product);
            Menu menuRequest = CREATE_TEST_MENU(product);
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

            // when
            product.setPrice(MINIMUM_PRICE);

            // then
            assertThatThrownBy(() -> menuService.create(menuRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴의 가격은 구성품 총 가격의 합보다 작아야 한다.");
        }

        @Test
        @DisplayName("메뉴의_이름은_빈값이면_안된다")
        void menuNameNotNull() {
            // given
            Menu menu = getMenuAndSaveMenuProduct();
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));

            // when
            menu.setName(null);

            // then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("부적절한 이름은 사용할 수 없습니다.");
        }

        @ParameterizedTest
        @CsvSource(value = {"fuck", "shit"})
        @DisplayName("메뉴의_이름은_부적절한_영어_이름이면_안된다")
        void profanityTest(String name) {
            // given
            Menu menu = getMenuAndSaveMenuProduct();
            given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
            given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

            // when
            menu.setName(name);

            // then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("부적절한 이름은 사용할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("기존_메뉴의_가격을_수정한다")
    class changePriceClass {

        @Test
        @DisplayName("기존_메뉴의_가격을_정상적으로_수정한다")
        void menuPriceChangeTest() {
            // given
            Menu menu = getMenuAndSaveMenuProduct();
            menuRepository.save(menu);

            // when
            menu.setPrice(MINIMUM_PRICE);
            Menu actual = menuService.changePrice(menu.getId(), menu);

            // then
            assertThat(actual.getPrice()).isEqualTo(MINIMUM_PRICE);
        }

        @Test
        @DisplayName("변경할_가격은_0원_이상이어야_한다")
        void changePriceTest() {
            // given
            Menu menu = getMenuAndSaveMenuProduct();

            // when
            menu.setPrice(new BigDecimal(-1));

            // then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("변경할 가격은 0이상이어야한다");
        }

        @Test
        @DisplayName("변경할 메뉴의 가격은 포함된 상품들의 각 금액(상품 가격 X 수량)의 합보다 높을 수 없다")
        void priceChangeTest() {
            // given
            Menu menu = getMenuAndSaveMenuProduct();
            menuRepository.save(menu);

            // when
            menu.setPrice(MAX_PRICE);

            // then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴의 가격은 구성품 총 가격의 합보다 작아야 한다.");
        }
    }

    @Nested
    @DisplayName("기존_메뉴가_사용자에게_보이도록_활성화한다")
    class menuDisplayClass {

        @Test
        @DisplayName("기존_메뉴가_사용자에게_보이도록_정상적으로_활성화한다")
        void menuDisplayTest() {
            // given
            Menu request = CREATE_TEST_MENU();
            menuRepository.save(request);

            // when
            request.setDisplayed(false);
            Menu actual = menuService.display(request.getId());

            // then
            assertThat(actual.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("활성화 시킬 메뉴의 가격은 포함된 상품들의 각 금액(상품 가격 X 수량)의 합보다 높을 수 없다")
        void displayChangeTest() {
            // given
            Menu request = CREATE_TEST_MENU();
            menuRepository.save(request);

            // when
            request.setPrice(MAX_PRICE);

            // then
            assertThatThrownBy(() -> menuService.display(request.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("메뉴의 가격은 구성품 총 가격의 합보다 작아야 한다.");
        }
    }

    @Test
    @DisplayName("기존_메뉴가_사용자에게_보이지_않도록_비활성화한다")
    void hideTest() {
        // given
        Menu menuRequest = CREATE_TEST_MENU();
        menuRequest.setDisplayed(true);
        menuRepository.save(menuRequest);

        // when
        Menu menu = menuService.hide(menuRequest.getId());

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    private Menu getMenuAndSaveMenuProduct() {
        Menu menuRequest = CREATE_TEST_MENU();
        productRepository.save(menuRequest.getMenuProducts().get(0).getProduct());
        return menuRequest;
    }

}
