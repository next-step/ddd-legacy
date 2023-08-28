package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    MenuService menuService;
    @Mock
    MenuRepository menuRepository;
    @Mock
    MenuGroupRepository menuGroupRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    @BeforeEach
    void setup() {
        this.menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴 신규시 가격이 입력되어야하고, 0이상이어야 한다.")
    @Test
    void price() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "menuGroupName");
        Product product = createProduct(UUID.randomUUID(), "productName", new BigDecimal(1));
        MenuProduct menuProduct = createMenuProduct(product, 0, UUID.randomUUID(), 0L);

        Menu menu = createMenu(UUID.randomUUID(), menuGroup, "test",
                new BigDecimal(-1), List.of(menuProduct), true, UUID.randomUUID());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 상품이 비어있으면 안된다.")
    @Test
    void product() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "GroupName");
        Product product = createProduct(UUID.randomUUID(), "productName", new BigDecimal(1));

        MenuProduct menuProduct = createMenuProduct(product, 1, UUID.randomUUID(),0L);

        Menu menu = createMenu(UUID.randomUUID(), menuGroup, "test",
                new BigDecimal(1000), List.of(menuProduct), true, UUID.randomUUID());
        menu.setMenuProducts(null);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품수량은 0 이상이다.")
    @Test
    void product_quantity() {
        Menu menu = createMenu(UUID.randomUUID(), new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(-1);

        menu.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("상품수량*가격이 메뉴 가격보다 비싸야한다.")
    @Test
    void menuPrice() {
        Menu menu = createMenu(UUID.randomUUID(), new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        Product product = createProduct(UUID.randomUUID(),"product",new BigDecimal(100));
        MenuProduct menuProduct = createMenuProduct(product,1,UUID.randomUUID(),0L);

        menu.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명이 비속어를 포함하면 안된다.")
    @Test
    void productName() {
        Menu menu = createMenu(UUID.randomUUID(), new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        Product product = createProduct(UUID.randomUUID(),null ,new BigDecimal(10000));
        MenuProduct menuProduct = createMenuProduct(product,1,UUID.randomUUID(),0L);

        menu.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("변경할 가격이 입력되어야하며, 0 이상이어야한다.")
    @Test
    void changePrice() {
        UUID menuId = UUID.randomUUID();
        Menu menu1 = createMenu(menuId, new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        Product product = createProduct(UUID.randomUUID(),"product",new BigDecimal(100));
        MenuProduct menuProduct = createMenuProduct(product,1,UUID.randomUUID(),0L);
        menu1.setMenuProducts(List.of(menuProduct));

        Menu menu2 = new Menu();
        menu2.setName("test2");
        menu2.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> menuService.changePrice(menuId,menu2))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("메뉴 가격은 내부 모든 메뉴 상품의 (가격*수량) 을 합한 값보다 작아야 한다.")
    @Test
    void changePrice2() {
        UUID menuId = UUID.randomUUID();
        Menu menu1 = createMenu(menuId, new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        Product product = createProduct(UUID.randomUUID(),"product",new BigDecimal(100));
        MenuProduct menuProduct = createMenuProduct(product,1,UUID.randomUUID(),0L);
        menu1.setMenuProducts(List.of(menuProduct));

        Menu menu2 = new Menu();
        menu2.setName("test2");
        menu2.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> menuService.changePrice(menuId,menu2))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("메뉴를 표시할 때, 메뉴 가격은 내부 모든 메뉴 상품의 (가격*수량) 을 합한 값보다 작아야 한다.")
    @Test
    void display() {
        UUID menuId = UUID.randomUUID();
        Menu menu = createMenu(menuId, new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        Product product = createProduct(UUID.randomUUID(),"product",new BigDecimal(100));
        MenuProduct menuProduct = createMenuProduct(product,1,UUID.randomUUID(),0L);
        menu.setMenuProducts(List.of(menuProduct));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("메뉴는 표시하거나 숨길 수 있다.(메뉴가 없는 경우 메뉴를 숨길 수 있다.)")
    @Test
    void displayAndHide() {
        UUID menuId = UUID.randomUUID();
        Menu menu = createMenu(menuId, new MenuGroup(), "test",
                new BigDecimal(1000), List.of(new MenuProduct()), true, UUID.randomUUID());

        Product product = createProduct(UUID.randomUUID(),"product",new BigDecimal(100));
        MenuProduct menuProduct = createMenuProduct(product,1,UUID.randomUUID(),0L);
        menu.setMenuProducts(List.of(menuProduct));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThat(menuService.hide(menu.getId()).isDisplayed()).isFalse();

    }


    public Menu createMenu(UUID menuGroupId, MenuGroup menuGroup, String name, BigDecimal price,
                           List<MenuProduct> menuProductList, boolean displayed, UUID id) {
        Menu menu = new Menu();
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setName(name);
        menu.setPrice(price);
        if (!menuProductList.isEmpty()) {
            menu.setMenuProducts(menuProductList);
        }
        menu.setDisplayed(displayed);
        menu.setId(id);
        return menu;
    }

    public MenuProduct createMenuProduct(Product product, long quantity, UUID productId, Long seq) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        menuProduct.setSeq(seq);
        return menuProduct;
    }

    public MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public Product createProduct(UUID id, String name, BigDecimal price){
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }




}
