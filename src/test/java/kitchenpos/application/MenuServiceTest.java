package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.integration_test_step.MenuGroupIntegrationStep;
import kitchenpos.integration_test_step.MenuIntegrationStep;
import kitchenpos.integration_test_step.ProductIntegrationStep;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import kitchenpos.test_fixture.ProductTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuService 클래스")
class MenuServiceTest {

    private MenuService sut;
    private MenuIntegrationStep menuIntegrationStep;
    private ProductIntegrationStep productIntegrationStep;
    private MenuGroupIntegrationStep menuGroupIntegrationStep;

    private MenuRepository menuRepository;
    private ProductRepository productRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProfanityClient profanityClient;

    @DisplayName("새로운 메뉴 등록")
    @Nested
    class Describe_create {

        @BeforeEach
        void setUp() {
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            profanityClient = new FakeProfanityClient();
            sut = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            productIntegrationStep = new ProductIntegrationStep(productRepository);
            menuGroupIntegrationStep = new MenuGroupIntegrationStep(menuGroupRepository);
        }

        @DisplayName("메뉴를 등록할 수 있다.")
        @Test
        void create() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when
            Menu result = sut.create(menu);

            // then
            assertNotNull(result);
            assertNotNull(result.getId());
            assertThat(result.getName()).isEqualTo("테스트 메뉴");
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(1000));
            assertThat(result.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
            assertThat(result.getMenuProducts())
                    .extracting("seq")
                    .doesNotContainNull();
            assertThat(result.isDisplayed()).isTrue();
        }

        @DisplayName("메뉴의 가격이 null이면 예외가 발생한다.")
        @Test
        void createWithNullPrice() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(null)
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("메뉴의 가격이 0보다 작은 음수면 예외가 발생한다.")
        @Test
        void createWithNegativeNumberPrice() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(BigDecimal.valueOf(-1))
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("메뉴에 등록하려는 메뉴 그룹이 존재하지 않으면 예외가 발생한다.")
        @Test
        void createWithNotExistMenuGroup() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup notPersistMenuGroup = MenuGroupTestFixture.create().getMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(notPersistMenuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.create(menu));
        }

        @DisplayName("등록하려는 메뉴에 메뉴 상품이 비어있으면 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createNotHaveMenuProduct(List<MenuProduct> menuProducts) {
            // given
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(menuProducts)
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("등록하려는 메뉴에 상품이 존재하지 않으면 예외가 발생한다.")
        @Test
        void createWithNotExistProduct() {
            // given
            Product notPersistProduct = ProductTestFixture.create().getProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(notPersistProduct)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("등록하려는 메뉴의 메뉴 상품 개수가 0보다 작으면 예외가 발생한다.")
        @Test
        void createWithNegativeNumberMenuProductQuantity() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .changeQuantity(-1L)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("등록하려는 메뉴의 가격이 메뉴 상품의 가격 합보다 크면 예외가 발생한다.")
        @Test
        void createWithPriceHigherThanMenuProductPrice() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())).add(BigDecimal.valueOf(1)))
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("등록하려는 메뉴의 이름이 null이면 예외가 발생한다.")
        @Test
        void createWithNullName() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changeName(null)
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("등록하려는 메뉴의 이름에 비속어가 포함되면 예외가 발생한다.")
        @Test
        void createWithProfanityName() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changeName("욕설이포함된메뉴이름") // `새끼` 라는 나쁜말 ^^
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(menu));
        }

        @DisplayName("메뉴를 등록할 때 전시 여부를 결정할 수 있다")
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void createWithDisplayed(boolean displayed) {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changeDisplayed(displayed)
                    .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                    .getMenu();

            // when
            Menu result = sut.create(menu);

            // then
            assertThat(result.isDisplayed()).isEqualTo(displayed);
        }
    }

    @DisplayName("메뉴 가격 변경 기능")
    @Nested
    class Describe_change_price {
        @BeforeEach
        void setUp() {
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            profanityClient = new FakeProfanityClient();
            sut = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            productIntegrationStep = new ProductIntegrationStep(productRepository);
            menuGroupIntegrationStep = new MenuGroupIntegrationStep(menuGroupRepository);
        }

        @DisplayName("메뉴의 가격을 변경할 수 있다.")
        @Test
        void changePrice() {
            // given
            Menu persistMenu = menuIntegrationStep.create();
            Menu updateMenu = MenuTestFixture.create()
                    .changeId(persistMenu.getId())
                    .changeName(persistMenu.getName())
                    .changeMenuGroup(persistMenu.getMenuGroup())
                    .changeMenuProducts(persistMenu.getMenuProducts())
                    .changePrice(persistMenu.getPrice().subtract(BigDecimal.valueOf(1)))
                    .changeDisplayed(persistMenu.isDisplayed())
                    .getMenu();

            // when
            Menu result = sut.changePrice(persistMenu.getId(), updateMenu);

            // then
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(999));
        }

        @DisplayName("메뉴 가격 변경 시 메뉴의 가격이 null이면 예외가 발생한다.")
        @Test
        void changePriceWithNullPrice() {
            // given
            Menu persistMenu = menuIntegrationStep.create();
            Menu updateMenu = MenuTestFixture.create()
                    .changeId(persistMenu.getId())
                    .changeName(persistMenu.getName())
                    .changeMenuGroup(persistMenu.getMenuGroup())
                    .changeMenuProducts(persistMenu.getMenuProducts())
                    .changePrice(null)
                    .changeDisplayed(persistMenu.isDisplayed())
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(persistMenu.getId(), updateMenu));
        }

        @DisplayName("메뉴 가격 변경 시 메뉴의 가격이 0보다 작으면 예외가 발생한다.")
        @Test
        void changePriceWithNegativeNumberPrice() {
            // given
            Menu persistMenu = menuIntegrationStep.create();
            Menu updateMenu = MenuTestFixture.create()
                    .changeId(persistMenu.getId())
                    .changeName(persistMenu.getName())
                    .changeMenuGroup(persistMenu.getMenuGroup())
                    .changeMenuProducts(persistMenu.getMenuProducts())
                    .changePrice(BigDecimal.valueOf(-1))
                    .changeDisplayed(persistMenu.isDisplayed())
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(persistMenu.getId(), updateMenu));
        }

        @DisplayName("메뉴 가격 변경 시 메뉴의 가격이 메뉴 상품의 가격 합보다 크면 예외가 발생한다.")
        @Test
        void changePriceWithPriceHigherThanMenuProductPrice() {
            // given
            Menu persistMenu = menuIntegrationStep.create();
            BigDecimal persistMenuPrice = persistMenu.getMenuProducts().stream()
                    .map(it -> it.getProduct().getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Menu updateMenu = MenuTestFixture.create()
                    .changeId(persistMenu.getId())
                    .changeName(persistMenu.getName())
                    .changeMenuGroup(persistMenu.getMenuGroup())
                    .changeMenuProducts(persistMenu.getMenuProducts())
                    .changePrice(persistMenuPrice.add(BigDecimal.ONE))
                    .changeDisplayed(persistMenu.isDisplayed())
                    .getMenu();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changePrice(persistMenu.getId(), updateMenu));
        }

        @DisplayName("메뉴 가격 변경 요청 시 가격 변경하려는 메뉴는 존재하는 메뉴여야 한다.")
        @Test
        void changePriceWithNotExistMenu() {
            // given
            Menu notPersistMenu = MenuTestFixture.create().getMenu();
            Menu updateMenu = MenuTestFixture.create()
                    .changeId(notPersistMenu.getId())
                    .changeName(notPersistMenu.getName())
                    .changeMenuGroup(notPersistMenu.getMenuGroup())
                    .changeMenuProducts(notPersistMenu.getMenuProducts())
                    .changePrice(notPersistMenu.getPrice().subtract(BigDecimal.valueOf(1)))
                    .changeDisplayed(notPersistMenu.isDisplayed())
                    .getMenu();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.changePrice(notPersistMenu.getId(), updateMenu));
        }
    }

    @DisplayName("메뉴 전시 상태 변경")
    @Nested
    class Describe_display {
        @BeforeEach
        void setUp() {
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            profanityClient = new FakeProfanityClient();
            sut = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            productIntegrationStep = new ProductIntegrationStep(productRepository);
            menuGroupIntegrationStep = new MenuGroupIntegrationStep(menuGroupRepository);
        }

        @DisplayName("메뉴를 전시 상태로 변경할 수 있다")
        @Test
        void changeDisplayed() {
            // given
            Menu persistMenu = menuIntegrationStep.create();

            // when
            Menu result = sut.display(persistMenu.getId());

            // then
            assertThat(result.isDisplayed()).isTrue();
        }

        @DisplayName("전시 상태를 변경하려는 메뉴는 존재하는 메뉴여야 한다.")
        @Test
        void changeDisplayedWithNotExistMenu() {
            // given
            Menu notPersistMenu = MenuTestFixture.create().getMenu();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.display(notPersistMenu.getId()));
        }

        @DisplayName("메뉴를 사용자에게 전시하려고 할 때 (메뉴의 가격 > 가지고 있는 상품의 총 가격) 이라면, 메뉴는 노출시킬 수 없다.")
        @Test
        void displayWithPriceHigherThanMenuProductPrice() {
            // given
            Product product = productIntegrationStep.createPersistProduct();
            MenuGroup menuGroup = menuGroupIntegrationStep.createPersistMenuGroup();
            MenuProduct menuProduct = MenuProductTestFixture.create()
                    .changeProduct(product)
                    .getMenuProduct();
            BigDecimal menuPrice = menuProduct.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(menuProduct.getQuantity()))
                    .add(BigDecimal.valueOf(1));
            Menu menu = MenuTestFixture.create()
                    .changeMenuGroup(menuGroup)
                    .changeMenuProducts(Collections.singletonList(menuProduct))
                    .changePrice(menuPrice)
                    .getMenu();
            menuRepository.save(menu);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.display(menu.getId()));
        }
    }

    @DisplayName("메뉴 숨김 상태 변경")
    @Nested
    class Describe_hide {
        @BeforeEach
        void setUp() {
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            profanityClient = new FakeProfanityClient();
            sut = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            productIntegrationStep = new ProductIntegrationStep(productRepository);
            menuGroupIntegrationStep = new MenuGroupIntegrationStep(menuGroupRepository);
        }

        @DisplayName("메뉴를 숨긴 상태로 변경할 수 있다")
        @Test
        void hide() {
            // given
            Menu persistMenu = menuIntegrationStep.create();

            // when
            Menu result = sut.hide(persistMenu.getId());

            // then
            assertThat(result.isDisplayed()).isFalse();
        }

        @DisplayName("숨긴 상태를 변경하려는 메뉴는 존재하는 메뉴여야 한다.")
        @Test
        void hideWithNotExistMenu() {
            // given
            Menu notPersistMenu = MenuTestFixture.create().getMenu();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.hide(notPersistMenu.getId()));
        }
    }

    @DisplayName("메뉴 목록 조회")
    @Nested
    class Describe_find_all {
        @BeforeEach
        void setUp() {
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            profanityClient = new FakeProfanityClient();
            sut = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            productIntegrationStep = new ProductIntegrationStep(productRepository);
            menuGroupIntegrationStep = new MenuGroupIntegrationStep(menuGroupRepository);
        }

        @DisplayName("메뉴 목록을 조회할 수 있다.")
        @Test
        void findAll() {
            // given
            Menu persistMenu = menuIntegrationStep.create();
            Menu persistMenu2 = menuIntegrationStep.create();

            // when
            List<Menu> result = sut.findAll();

            // then
            assertThat(result).hasSize(2)
                    .extracting("id")
                    .containsAll(List.of(persistMenu.getId(), persistMenu2.getId()));
        }
    }
}
