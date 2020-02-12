package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

    @InjectMocks
    private MenuBo menuBo;

    @Mock
    private MenuProductDao menuProductDao; // TODO

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private ProductDao productDao;

    @Test
    @DisplayName("메뉴 리스트 조회")
    void list() {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(1L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();
        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu1 = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1))
                .build();

        Menu menu2 = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct2))
                .build();

        given(menuDao.findAll())
                .willReturn(Arrays.asList(menu1, menu2));

        List<Menu> list = menuBo.list();

        assertThat(list.get(0).getId()).isEqualTo(1L);
        assertThat(list.size()).isEqualTo(2);
    }


    @Test
    @DisplayName("메뉴 생성 정상 동작")
    void create() {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(1L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();
        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build();


        menu.getMenuProducts()
                .forEach(menuProduct -> {
                    Product product = new Product();
                    product.setPrice(BigDecimal.TEN);
                    given(productDao.findById(menuProduct.getProductId())).willReturn(Optional.of(product));
                });

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(menuDao.save(menu)).willReturn(menu);

        Menu savedMenu = menuBo.create(menu);

        assertThat(savedMenu.getId()).isEqualTo(menu.getId());
        assertThat(savedMenu.getPrice()).isEqualTo(menu.getPrice());
    }

    @Test
    @DisplayName("메뉴 가격이 음수 일때 생성 실패")
    void createFailByNegativePrice() {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(1L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();
        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.valueOf(-1L))
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build();

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    @DisplayName("메뉴 그룹 정보가 없을때 메뉴 생성 실패")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 9999)
    void createFailByNotExsistMenugroup(Long value) {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(1L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();
        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu = new MenuBuilder()
                .setMenuGroupId(value)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build();

        menu.setMenuGroupId(value);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    @DisplayName("메뉴 가격이 Null 일때 생성 실패")
    @ParameterizedTest
    @NullSource
    void createFailByNull(BigDecimal price) {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(1L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();
        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(price)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build();

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("등록되지 않은 상품을 포함 시 생성 실패")
    @ParameterizedTest
    @ValueSource(longs = {999, 888})
    void createFailByNotExistProduct(Long value) {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(value)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1))
                .build();

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴 가격이 상품의 가격 합보다 클때 생성 실패")
    void createFailByPriceGreaterThanProductPrice() {
        MenuProduct menuProduct1 = new MenuProductBuilder()
                .setProductId(1L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build();

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1))
                .build();

        menu.getMenuProducts()
                .forEach(menuProduct -> {
                    Product product = new Product();
                    product.setPrice(BigDecimal.ONE);
                    given(productDao.findById(menuProduct.getProductId())).willReturn(Optional.of(product));
                });

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    public static class MenuBuilder {
        private Long id;
        private String name;
        private BigDecimal price;
        private Long menuGroupId;
        private List<MenuProduct> menuProducts;

        public MenuBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public MenuBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MenuBuilder setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public MenuBuilder setMenuGroupId(Long menuGroupId) {
            this.menuGroupId = menuGroupId;
            return this;
        }

        public MenuBuilder setMenuProducts(List<MenuProduct> menuProducts) {
            this.menuProducts = menuProducts;
            return this;
        }

        public Menu build() {
            Menu menu = new Menu();
            menu.setId(id);
            menu.setPrice(price);
            menu.setName(name);
            menu.setMenuGroupId(menuGroupId);
            menu.setMenuProducts(menuProducts);
            return menu;
        }
    }

    public static class MenuProductBuilder {
        private Long seq;
        private Long menuId;
        private Long productId;
        private long quantity;

        public MenuProductBuilder setSeq(Long seq) {
            this.seq = seq;
            return this;
        }

        public MenuProductBuilder setMenuId(Long menuId) {
            this.menuId = menuId;
            return this;
        }

        public MenuProductBuilder setProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public MenuProductBuilder setQuantity(long quantity) {
            this.quantity = quantity;
            return this;
        }

        public MenuProduct build() {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setMenuId(menuId);
            menuProduct.setQuantity(quantity);
            menuProduct.setSeq(seq);
            return menuProduct;
        }
    }
}

