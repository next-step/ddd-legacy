package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Nested
    @DisplayName("메뉴 등록")
    class RegisterMenu {

        @Test
        @DisplayName("메뉴를 등록한다.")
        void registerMenu() {
            // given
            final Product product = saveProduct("후라이드", BigDecimal.valueOf(16_000));
            final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
            final Menu request = MenuFixture.createMenu("후라이드 치킨", BigDecimal.valueOf(16_000), List.of(menuProduct), menuGroup);
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when
            final Menu result = menuService.create(request);

            // then
            final Menu found = menuRepository.findById(result.getId()).orElse(null);

            assertThat(found).isNotNull();
            assertAll(
                    () -> assertThat(found.getName()).isEqualTo(request.getName()),
                    () -> assertThat(found.getPrice()).isEqualByComparingTo(request.getPrice())
            );
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("메뉴는 이름과 가격을 필수로 가진다.")
        void nullName(final String name) {
            // given
            final Product product = saveProduct("후라이드", BigDecimal.valueOf(16_000));
            final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
            final Menu request = MenuFixture.createMenu(name, BigDecimal.valueOf(16_000), List.of(menuProduct), menuGroup);
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when & then
            assertThatException()
                    .isThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("메뉴의 가격은 0원 이상이어야 한다.")
        @ValueSource(ints = {-1000, -1})
        void priceLessThanZero(final int price) {
            // given
            final Product product = saveProduct("후라이드", BigDecimal.valueOf(16_000));
            final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
            final Menu request = MenuFixture.createMenu("후라이드치킨", BigDecimal.valueOf(price), List.of(menuProduct), menuGroup);
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when & then
            assertThatException()
                    .isThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 등록 시 이름의 유해성 여부를 검사한다.")
        void inappropriateName() {
            // given
            final Product product = saveProduct("후라이드", BigDecimal.valueOf(16_000));
            final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
            final Menu request = MenuFixture.createMenu("부적절한이름", BigDecimal.valueOf(16_000), List.of(menuProduct), menuGroup);
            given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

            // when & then
            assertThatException()
                    .isThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangeMenuPrice {
        private UUID existingId;
        private UUID nonExistingId = UUID.randomUUID();

        @BeforeEach
        void setup() {
            final Product product = saveProduct("후라이드", BigDecimal.valueOf(16_000));
            final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
            final Menu request = MenuFixture.createMenu("후라이드 치킨", BigDecimal.valueOf(16_000), List.of(menuProduct), menuGroup);
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
            existingId = menuService.create(request).getId();
        }

        @Test
        @DisplayName("지정한 메뉴의 가격을 변경할 수 있다.")
        void changeMenuPriceSuccess() {
            // given
            final Menu request = MenuFixture.createMenuRequest(BigDecimal.valueOf(15_000));

            // when
            final Menu result = menuService.changePrice(existingId, request);

            // then
            final Menu found = menuRepository.findById(result.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertThat(found.getPrice()).isEqualByComparingTo(request.getPrice());
        }

        @ParameterizedTest
        @DisplayName("메뉴 가격 변경 시 가격이 0원 이상이어야 한다.")
        @ValueSource(ints = {-1000, -1})
        void changeMenuPriceFailsWithNegativePrice(final int price) {
            // given
            final Menu request = MenuFixture.createMenuRequest(BigDecimal.valueOf(price));

            // when & then
            assertThatException()
                    .isThrownBy(() -> menuService.changePrice(existingId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("등록되지 않은 메뉴의 가격을 변경할 수 없다.")
        void nonExistingMenu() {
            // given
            final Menu request = MenuFixture.createMenuRequest(BigDecimal.valueOf(15_000));

            // when & then
            assertThatException()
                    .isThrownBy(() -> menuService.changePrice(nonExistingId, request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 조회")
    class FindAllMenus {

        @BeforeEach
        void setup() {
            final Product product = saveProduct("후라이드", BigDecimal.valueOf(16_000));
            final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
            final Menu menu = MenuFixture.createMenu("후라이드 치킨", BigDecimal.valueOf(16_000), List.of(menuProduct), menuGroup);
        }

        @Test
        @DisplayName("등록된 모든 메뉴의 목록을 조회한다.")
        void findAllMenusSuccess() {
            // given
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
            List<UUID> menuIds = IntStream.rangeClosed(1, 2).mapToObj(i -> {
                final Product product = saveProduct(i, "후라이드", BigDecimal.valueOf(16_000));
                final MenuGroup menuGroup = saveMenuGroup("한마리메뉴");
                final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1L);
                final Menu menu = MenuFixture.createMenu("후라이드 치킨" + i, BigDecimal.valueOf(16_000), List.of(menuProduct), menuGroup);
                return menuService.create(menu).getId();
            }).toList();

            // when
            final List<Menu> result = menuService.findAll();

            // then
            assertThat(result)
                    .hasSize(2)
                    .extracting(Menu::getId)
                    .containsExactly(menuIds.toArray(UUID[]::new));
        }
    }

    private Product saveProduct(int index, String name, BigDecimal price) {
        return productRepository.save(
                ProductFixture.createProduct(name + index, price)
        );
    }

    private Product saveProduct(String name, BigDecimal price) {
        return saveProduct(0, name, price);
    }

    private MenuGroup saveMenuGroup(String name) {
        return menuGroupRepository.save(
                MenuGroupFixture.createMenuGroup(name)
        );
    }

}
