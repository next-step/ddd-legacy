package kitchenpos.application.old;

import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OldMenuServiceTest {

    private final BigDecimal menuPrice = BigDecimal.valueOf(39000L);

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MenuService menuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        Menu request = getMenuRequest(menuPrice);
        Menu menu = menuService.create(request);
        assertThat(menu).isNotNull();
    }

    @DisplayName("메뉴 생성시 price validation")
    @Test
    void createValidationPrice() {

        // negative price
        Menu negativePriceRequest = getMenuRequest(menuPrice);
        negativePriceRequest.setPrice(convertToNegative(negativePriceRequest.getPrice()));
        assertThatThrownBy(() -> menuService.create(negativePriceRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // null price
        Menu nullPriceRequest = getMenuRequest(menuPrice);
        nullPriceRequest.setPrice(null);
        assertThatThrownBy(() -> menuService.create(nullPriceRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 생성시 menuGroup validation")
    @Test
    void createValidationMenuGroup() {

        // not exists menuGroup
        Menu noExistsMenuGroupRequest = getMenuRequest(menuPrice);
        noExistsMenuGroupRequest.setMenuGroupId(UUID.randomUUID());
        assertThatThrownBy(() -> menuService.create(noExistsMenuGroupRequest))
                .isInstanceOf(NoSuchElementException.class);

    }


    @DisplayName("메뉴 생성시 미존재 product validation")
    @Test
    void createValidationProduct() {

        Menu menuRequest = getMenuRequest(menuPrice);
        menuRequest.getMenuProducts().forEach(
                menuProduct -> menuProduct.setProductId(UUID.randomUUID())
        );
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 생성시 menuProducts validation")
    @Test
    void createValidationMenuProducts() {

        Menu menuRequest = getMenuRequest(menuPrice);

        // null MenuProducts
        menuRequest.setMenuProducts(null);
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // empty MenuProducts
        menuRequest.setMenuProducts(Collections.emptyList());
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 생성시 menuProducts quantity, price validation")
    @Test
    void createValidationMenuProductsDetail() {

        // quantity < 0
        Menu quantityUnderZeroRequest = getMenuRequest(menuPrice);
        quantityUnderZeroRequest.getMenuProducts().forEach(
                menuProduct -> menuProduct.setQuantity(0)
        );
        assertThatThrownBy(() -> menuService.create(quantityUnderZeroRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // price sum
        Menu expensiveMenuRequest = getMenuRequest(menuPrice);
        BigDecimal menuProductsPriceSum = BigDecimal.ZERO;
        List<BigDecimal> prices = expensiveMenuRequest.getMenuProducts().stream()
                .map(menuProduct ->
                        menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .collect(Collectors.toList());
        for (BigDecimal price : prices) {
            menuProductsPriceSum = menuProductsPriceSum.add(price);
        }
        expensiveMenuRequest.setPrice(menuProductsPriceSum.add(menuPrice));
        assertThatThrownBy(() -> menuService.create(expensiveMenuRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 생성시 메뉴이름 validation (null)")
    @NullSource
    @ParameterizedTest
    void createValidationMenuNameNull(String name) {
        Menu menuRequest = getMenuRequest(menuPrice);
        menuRequest.setName(name);

        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성시 메뉴이름 validation (부정적 단어)")
    @ValueSource(strings = "shit")
    @NullSource
    @ParameterizedTest
    void createValidationMenuNameProfanity(String name) {
        Menu menuRequest = getMenuRequest(menuPrice);
        menuRequest.setName(name);

        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경")
    @Test
    void changePrice() {
        Menu request = getMenuRequest(menuPrice);
        Menu menu = menuService.create(request);
        final BigDecimal changePrice = BigDecimal.valueOf(30000L);
        menu.setPrice(changePrice);
        Menu changedMenu = menuService.changePrice(menu.getId(), menu);

        assertThat(changedMenu.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("메뉴 가격 변경시 price validation")
    @Test
    void changePriceValidationPrice() {
        Menu request = getMenuRequest(menuPrice);
        Menu menu = menuService.create(request);

        // minus price
        menu.setPrice(convertToNegative(menu.getPrice()));
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);

        // null price
        menu.setPrice(null);
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경시 menu validation")
    @Test
    void changePriceValidationNotExistsMenu() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(menuPrice);

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 가격 변경시 단품 가격의 합과 메뉴 가격 비교 validation")
    @Test
    void changePriceValidationPriceSum() {

        Menu menu = menuService.create(getMenuRequest(menuPrice));
        BigDecimal menuProductsPriceSum = BigDecimal.ZERO;
        List<BigDecimal> prices = menu.getMenuProducts().stream()
                .map(menuProduct ->
                        menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .collect(Collectors.toList());
        for (BigDecimal price : prices) {
            menuProductsPriceSum = menuProductsPriceSum.add(price);
        }

        Menu priceChangeRequest = new Menu();
        priceChangeRequest.setId(menu.getId());
        priceChangeRequest.setPrice(menuProductsPriceSum.add(menuPrice));

        assertThatThrownBy(() -> menuService.changePrice(priceChangeRequest.getId(), priceChangeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 노출")
    @Test
    void display() {
        Menu menu = menuService.create(getMenuRequest(menuPrice));
        Menu visibleMenu = menuService.display(menu.getId());
        assertThat(visibleMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 숨김")
    @Test
    void hide() {
        Menu menu = menuService.create(getMenuRequest(menuPrice));
        Menu unVisibleMenu = menuService.hide(menu.getId());
        assertThat(unVisibleMenu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 숨김시 미존재 메뉴 validation")
    @Test
    void hideValidationNotExistsMenu() {
        assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    void findAll() {
        assertThat(menuRepository.findAll().size())
                .isEqualTo(menuService.findAll().size());
    }

    private Menu getMenuRequest(BigDecimal price) {
        MenuGroup menuGroup = saveMenuGroup();
        Product product = saveProduct();

        List<MenuProduct> menuProducts = List.of(new MenuProduct(product, 2));
        final String menuName = "순살두마리세트";

        Menu request = new Menu();
        request.setName(menuName);
        request.setPrice(price);
        request.setMenuGroupId(menuGroup.getId());
        request.setDisplayed(true);
        request.setMenuProducts(menuProducts);
        return request;
    }

    private MenuGroup saveMenuGroup() {
        final String menuGroupName = "세트류";
        return menuGroupService.create(new MenuGroup(menuGroupName));
    }

    private Product saveProduct() {
        final String productName = "순살치킨";
        final BigDecimal productPrice = BigDecimal.valueOf(20000L);
        return productService.create(new Product(productName, productPrice));
    }

    private BigDecimal convertToNegative(BigDecimal bigDecimal) {
        return bigDecimal.multiply(BigDecimal.valueOf(-1L));
    }

}
