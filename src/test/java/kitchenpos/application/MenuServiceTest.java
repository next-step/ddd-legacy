package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.application.fixture.MenuFixture.HIDE_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.SHOW_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_ONE_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT_ONE_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("메뉴 등록 성공")
    @Test
    void createMenuSuccess() {
        // Given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        final BigDecimal price = BigDecimal.valueOf(15000);
        final Product product = productRepository.save(PRODUCT_ONE_REQUEST());
        final long productQuantity = 1;

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setName("맛있는 순살 치킨");
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // When
        Menu result = menuService.create(menu);

        // Then
        Menu menuData = menuRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.getId()).isEqualTo(menuData.getId());
    }

    @DisplayName("메뉴 등록 실패 - 음수 가격 입력")
    @Test
    void createMenuFailMinusPrice() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        final BigDecimal price = BigDecimal.valueOf(-1);

        final Menu request = new Menu();
        request.setName("맛있는 뼈치킨");
        request.setMenuGroupId(MENU_GROUP_ONE_REQUEST().getId());
        request.setPrice(price);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 등록 실패 - 등록되지 않은 menuGroupId")
    @Test
    void createMenuFailNonExistentMenuGroupId() {
        // Given
        final BigDecimal price = BigDecimal.valueOf(-1);

        final Menu request = new Menu();
        request.setName("맛있는 뼈치킨");
        request.setMenuGroupId(UUID.randomUUID());
        request.setPrice(price);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 등록 실패 - 등록되지 않은 productId")
    @Test
    void createMenuNonExistentProductId() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        final BigDecimal price = BigDecimal.valueOf(15000);
        final long productQuantity = 1;

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setName("맛있는 순살 치킨");
        menu.setMenuGroupId(MENU_GROUP_ONE_REQUEST().getId());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 등록 실패 - 메뉴상품의 수량이 음수인 경우")
    @Test
    void createMenuFailMinusMenuProductQuantity() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        final BigDecimal price = BigDecimal.valueOf(15000);
        final long productQuantity = -1;

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ONE_REQUEST().getId());
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setName("맛있는 순살 치킨");
        menu.setMenuGroupId(MENU_GROUP_ONE_REQUEST().getId());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 등록 실패 - 메뉴의 가격이 상품의 가격 * 수량보다 큰 경우")
    @Test
    void createMenuFailPrice() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());

        final BigDecimal price = BigDecimal.valueOf(17000);
        final long productQuantity = 1;

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ONE_REQUEST().getId());
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setName("맛있는 순살 치킨");
        menu.setMenuGroupId(MENU_GROUP_ONE_REQUEST().getId());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 등록 실패 - 빈 메뉴 이름 또는 비속어")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"fuck"})
    void createMenuFileNameNullOrVulgarism(final String name) {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());

        final BigDecimal price = BigDecimal.valueOf(15000);
        final long productQuantity = 1;

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ONE_REQUEST().getId());
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroupId(MENU_GROUP_ONE_REQUEST().getId());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 가격 변경 성공")
    @Test
    void changePriceSuccess() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());

        final BigDecimal price = BigDecimal.valueOf(15000);
        final long productQuantity = 1;

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ONE_REQUEST().getId());
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setName("맛있는 순살 치킨");
        menu.setMenuGroupId(MENU_GROUP_ONE_REQUEST().getId());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        final Menu changeMenu = new Menu();
        changeMenu.setPrice(BigDecimal.valueOf(16000));

        // When
        Menu result = menuService.create(menu);
        menuService.changePrice(result.getId(), changeMenu);
        Menu changeMenuData = menuRepository.findById(result.getId())
                .orElseThrow(NoSuchElementException::new);
        BigDecimal expectedPrice = changeMenu.getPrice();
        BigDecimal actualPrice = changeMenuData.getPrice();

        // Then
        assertThat(actualPrice).isEqualTo(expectedPrice);
    }

    @DisplayName("메뉴 가격 변경 실패 - 등록되지 않은 메뉴 ID")
    @Test
    void changePriceFailNonExistentMenuId() {
        // Given
        final UUID menuGroupId = UUID.randomUUID();
        final BigDecimal price = BigDecimal.valueOf(15000);

        final Menu changeMenu = new Menu();
        changeMenu.setPrice(price);

        // When, Then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(menuGroupId, changeMenu));
    }

    @DisplayName("메뉴 가격 변경 실패 - 음수 또는 메뉴의 가격이 상품 * 수량보다 큰 경우")
    @ParameterizedTest
    @ValueSource(longs = {-1, 17000})
    void changePriceFailInvalidPrice(final long price) {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());

        final Menu changeMenu = new Menu();
        changeMenu.setPrice(BigDecimal.valueOf(price));

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(SHOW_MENU_REQUEST().getId(), changeMenu));
    }

    @DisplayName("메뉴 노출 처리")
    @Test
    void displayMenu() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(HIDE_MENU_REQUEST());

        // When
        Menu data = menuService.display(HIDE_MENU_REQUEST().getId());

        // Then
        assertThat(data.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 숨김 처리")
    @Test
    void hideMenu() {
        // Given
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());

        // When
        Menu data = menuService.hide(SHOW_MENU_REQUEST().getId());

        // Then
        assertThat(data.isDisplayed()).isFalse();
    }

    @DisplayName("전체 메뉴 조회")
    @Test
    void findAllMenus() {
        // Given

        // When
        List<Menu> menus = menuService.findAll();

        // Then
        assertThat(menus).isNotEmpty();

    }
}
