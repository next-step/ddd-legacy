package kitchenpos.application.menu;


import kitchenpos.application.*;
import kitchenpos.application.Exception.*;
import kitchenpos.domain.*;
import kitchenpos.fake.FakePurogmalumClient;
import kitchenpos.fake.repository.InMemoryMenuGroupRepository;
import kitchenpos.fake.repository.InMemoryMenuRepository;
import kitchenpos.fake.repository.InMemoryProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuServiceTest {
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        PurgomalumClient purgomalumClient = new FakePurogmalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("메뉴 등록")
    class CreateMenu {

        @Test
        @DisplayName("성공")
        void success() {
            // given
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(menuProduct), 1000, menuGroup.getId());

            Menu created = menuService.create(request);
            // then
            assertAll(
                    () -> assertThat(created.getId()).isNotNull(),
                    () -> assertThat(created.getName()).isEqualTo("콜라 세트"),
                    () -> assertThat(created.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1000)),
                    () -> assertThat(created.isDisplayed()).isEqualTo(true),
                    () -> assertThat(created.getMenuProducts()).hasSize(1)
            );
        }

        @DisplayName("메뉴 가격이 0원 미만이면 실패")
        @ValueSource(ints = {-1000, -1})
        @ParameterizedTest
        void failWithNegativePrice(int price) {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(menuProduct), price, menuGroup.getId());


            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(PriceInvalidException.class);
        }

        @Test
        @DisplayName("메뉴 그룹이 존재하지 않으면 실패")
        void failWithNonExistentMenuGroup() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(menuProduct), 1000, menuGroup.getId());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(MenuGroupNoExist.class);
        }

        @Test
        @DisplayName("메뉴 상품이 비어있으면 실패")
        void failWithEmptyMenuProducts() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(), 1000, menuGroup.getId());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(MenuProductEmptyException.class);
        }

        @Test
        @DisplayName("저장된 프로덕트 사이즈와 요청한 메뉴 상품 사이즈가 다르면 실패")
        void failWithProductNumberNotEqualWithRequests() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            Product cider = ProductFixture.product("사이다", 1000);
            productRepository.save(cola);  // Fake repository에 저장

            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장
            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            MenuProduct menuProduct2 = MenuFixture.menuProduct(cider, 1);

            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(menuProduct, menuProduct2), 1000, menuGroup.getId());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(InvalidMenuProducts.class);
        }

        //
        @DisplayName("메뉴 상품 수량이 0개 미만이면 실패")
        @ValueSource(ints = {-1, -1000})
        @ParameterizedTest
        void failWithNegativeQuantity(int quantity) {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장

            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장
            MenuProduct menuProduct = MenuFixture.menuProduct(cola, quantity);

            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(menuProduct), 1000, menuGroup.getId());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(MenuProductQuantityInvalidException.class);
        }

        @Test
        @DisplayName("메뉴 가격이 구성 상품 가격 총합보다 크면 실패")
        void failWithPriceGreaterThanSum() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu request = MenuFixture.menuWithDisplayTrue("콜라 세트", List.of(menuProduct), 2000, menuGroup.getId());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(MenuPriceInvalidException.class);
        }

        @DisplayName("메뉴 이름에 비속어가 포함되면 실패")
        @ValueSource(strings = {"바보", "멍청이"})
        @ParameterizedTest
        void failWithProfanity(String menuName) {
            // given
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu request = MenuFixture.menuWithDisplayTrue(menuName, List.of(menuProduct), 1000, menuGroup.getId());

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(MenuNameInvalidException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangePrice {
        @Test
        @DisplayName("성공")
        void success() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("코오올라 세트", List.of(menuProduct), 1000, menuGroup.getId());
            menuRepository.save(menu);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(1000));

            Menu updated = menuService.changePrice(menu.getId(), request);

            assertThat(updated.getPrice()).isEqualByComparingTo("1000");
        }

        @Test
        @DisplayName("가격이 0원 미만이면 실패")
        void failWithNegativePrice() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("코오올라 세트", List.of(menuProduct), 1000, menuGroup.getId());
            menuRepository.save(menu);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(-1000));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                    .isInstanceOf(PriceInvalidException.class);
        }

        @Test
        @DisplayName("가격이 구성 상품 가격 총합보다 크면 실패")
        void failWithPriceGreaterThanSum() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("코오올라 세트", List.of(menuProduct), 1000, menuGroup.getId());
            menuRepository.save(menu);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(2000));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                    .isInstanceOf(MenuPriceInvalidException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 노출")
    class DisplayMenu {
        @Test
        @DisplayName("성공")
        void success() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("코오올라 세트", List.of(menuProduct), 1000, menuGroup.getId());
            menuRepository.save(menu);


            Menu displayed = menuService.display(menu.getId());

            assertThat(displayed.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 메뉴를 숨길 수 없다.")
        void failWithPriceGreaterThanSum() {
            Product cola = ProductFixture.product("코카 콜라", 1000);
            productRepository.save(cola);  // Fake repository에 저장
            MenuGroup menuGroup = MenuFixture.menuGroup("음료");
            menuGroupRepository.save(menuGroup);  // Fake repository에 저장

            MenuProduct menuProduct = MenuFixture.menuProduct(cola, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("코오올라 세트", List.of(menuProduct), 1000, menuGroup.getId());


            assertThatThrownBy(() -> menuService.display(menu.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}