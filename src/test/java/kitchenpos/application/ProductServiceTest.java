package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupService menuGroupService;

    //region [상품 등록]
    @DisplayName("상품명과 가격을 입력하여 상품을 생성한다")
    @Test
    void createProduct() {
        Product request = createProduct("치킨버거", new BigDecimal(7000));

        Product product = productService.create(request);

        assertThat(product)
                .extracting(Product::getName, Product::getPrice)
                .containsExactly("치킨버거", new BigDecimal(7000));
    }

    @DisplayName("상품명은 반드시 입력되어야 한다")
    @Test
    void notNullProductName() {
        Product request = createProduct(null, new BigDecimal(7000));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("가격은 반드시 입력되어야 한다")
    @Test
    void notNullProductPrice() {
        Product request = createProduct("치킨버거", null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("상품명은 비속어가 있으면 등록할 수 없다")
    @Test
    void validateProductName() {
        Mockito.when(purgomalumClient.containsProfanity("bad word")).thenReturn(true);
        Product request = createProduct("bad word", new BigDecimal(7000));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("상품의 가격은 0원 이상이어야 한다")
    @Test
    void validatePriceOnCreate() {
        Product request = createProduct("치킨버거", new BigDecimal(-1));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(request));
    }
    //endregion

    //region [상품 수정]
    @DisplayName("상품의 가격을 수정할 수 있다")
    @Test
    void modifyPrice() {
        Product product = productService.create(createProduct(UUID.randomUUID(), "치킨버거", new BigDecimal(7000)));

        product.setPrice(new BigDecimal(8000));
        Product modifiedProduct = productService.changePrice(product.getId(), product);

        assertThat(modifiedProduct.getPrice()).isEqualTo(new BigDecimal(8000));
    }

    @DisplayName("상품의 가격은 0원 이상인 경우만 수정 가능하다")
    @Test
    void validatePriceOnModify() {
        Product product = productService.create(createProduct(UUID.randomUUID(), "치킨버거", new BigDecimal(7000)));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.changePrice(product.getId(), createProduct("치킨버거", new BigDecimal(-1))));
    }

    @DisplayName("메뉴의 가격이 메뉴에 속한 상품들의 가격 총 합보다 클 경우, 유효하지 않은 메뉴이므로 메뉴판에 전시할 수 없다")
    @Test
    void validatePriceBySetMenu() {
        //given
        Product chicken = productService.create(createProduct("후라이드치킨", new BigDecimal(25000)));
        Product coke = productService.create(createProduct("콜라", new BigDecimal(2500)));

        MenuGroup menuGroup = menuGroupService.create(createMenuGroup("세트메뉴"));

        MenuProduct chickenMenuProduct = createMenuProduct(chicken, 1);
        MenuProduct cokeMenuProduct = createMenuProduct(coke, 1);

        BigDecimal productSum = chicken.getPrice().add(coke.getPrice());
        Menu menu = createMenu(
                menuGroup.getId(),
                "후라이드치킨세트",
                productSum,
                List.of(chickenMenuProduct, cokeMenuProduct)
        );
        Menu chickenSet = menuService.create(menu);
        //when
        BigDecimal newPrice = chicken.getPrice().add(new BigDecimal(1000));
        chicken.setPrice(newPrice);
        productService.changePrice(chicken.getId(), chicken);
        //when
        assertThat(chickenSet.isDisplayed()).isFalse();
    }
    //endregion

    //region [메뉴 조회]
    @DisplayName("모든 메뉴를 조회할 수 있다")
    @Test
    void findAll() {
        productService.create(createProduct("치킨버거", new BigDecimal(7000)));
        productService.create(createProduct("새우버거", new BigDecimal(8000)));

        List<Product> products = productService.findAll();

        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting(Product::getName, product -> product.getPrice().intValue())
                .contains(
                        Tuple.tuple("치킨버거", 7000),
                        Tuple.tuple("새우버거", 8000)
                );
    }
    //endregion

    private Product createProduct(String name, BigDecimal price) {
        return createProduct(null, name, price);
    }

    private Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    private Menu createMenu(UUID menuGroupId, String name, BigDecimal price, List<MenuProduct> products) {
        Menu menu = new Menu();
        menu.setMenuGroupId(menuGroupId);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(products);
        return menu;
    }
}
