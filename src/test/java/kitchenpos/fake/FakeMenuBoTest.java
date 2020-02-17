package kitchenpos.fake;

import kitchenpos.bo.MenuBo;
import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.MenuProductBuilder;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeMenuBoTest {
    private MenuBo menuBo;

    private MenuProductDao menuProductDao = new FakeMenuProductDao();

    private MenuDao menuDao = new FakeMenuDao();

    private MenuGroupDao menuGroupDao = new FakeMenuGroupDao();

    private ProductDao productDao = new FakeProductDao();

    @BeforeEach
    void setUp() {
        this.menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
    }

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
                .setId(2L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct2))
                .build();

        menuDao.save(menu1);
        menuDao.save(menu2);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

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
                .build()
                ;

        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build()
                ;

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build()
                ;

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("간장 메뉴");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.TEN);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setPrice(BigDecimal.ONE);

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);
        menuGroupDao.save(menuGroup);
        productDao.save(product1);
        productDao.save(product2);


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
                .build()
                ;

        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build()
                ;

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.valueOf(-1L))
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build()
                ;

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

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
                .build()
                ;

        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build()
                ;

        Menu menu = new MenuBuilder()
                .setMenuGroupId(value)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build()
                ;

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

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
                .build()
                ;

        MenuProduct menuProduct2 = new MenuProductBuilder()
                .setProductId(2L)
                .setMenuId(1L)
                .setSeq(1L)
                .setQuantity(2)
                .build()
                ;

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(price)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1, menuProduct2))
                .build()
                ;

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

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
                .build()
                ;

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1))
                .build()
                ;

        menuDao.save(menu);

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
                .build()
                ;

        Menu menu = new MenuBuilder()
                .setMenuGroupId(1L)
                .setId(1L)
                .setPrice(BigDecimal.TEN)
                .setName("간장치킨")
                .setMenuProducts(Arrays.asList(menuProduct1))
                .build()
                ;

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }
}
