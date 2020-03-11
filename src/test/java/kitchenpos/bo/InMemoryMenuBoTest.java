package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import kitchenpos.support.MenuBuilder;
import kitchenpos.support.MenuGroupBuilder;
import kitchenpos.support.MenuProductBuilder;
import kitchenpos.support.ProductBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InMemoryMenuBoTest {
    private final MenuDao menuDao = new InMemoryMenuDao();
    private final MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    private final MenuProductDao menuProductDao = new InMemoryMenuProductDao();
    private final ProductDao productDao = new InMemoryProductDao();

    private MenuBo menuBo;

    @BeforeEach
    void setup(){
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);

        Product product1 = new ProductBuilder()
            .id(1L)
            .name("뿌링클")
            .price(BigDecimal.valueOf(10000L))
            .build();
        productDao.save(product1);

        Product product2 = new ProductBuilder()
            .id(2L)
            .name("후라이드치킨")
            .price(BigDecimal.valueOf(15000L))
            .build();
        productDao.save(product2);

        MenuGroup menuGroup = new MenuGroupBuilder()
            .id(1L)
            .name("두마리치킨")
            .build();
        menuGroupDao.save(menuGroup);
    }

    @DisplayName("메뉴가격을 잘못 입력하면 안된다.")
    @ValueSource(longs = {-1000, -10, 0})
    @ParameterizedTest
    void createWithWrongPrice (long price){
        Menu menu = new MenuBuilder()
            .id(1L)
            .price(BigDecimal.valueOf(price))
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴그룹을 잘못 설정하면 메뉴 등록이 안된다.")
    @Test
    void createWithWrongMenuGroup (){
        Menu menu = new MenuBuilder()
            .id(1L)
            .price(BigDecimal.valueOf(10000))
            .menuGroupId(2L)
            .build();

        MenuGroup menuGroup = new MenuGroupBuilder()
            .id(1L)
            .build();

        menuGroupDao.save(menuGroup);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴가격은 메뉴를 구성하는 상품 가격의 합보다 작거나 같아야한다.")
    @ValueSource(longs = {21000, 100000})
    @ParameterizedTest
    void createPriceUnderAllProductsPrice(final long price){
        MenuProduct menuProduct = new MenuProductBuilder()
            .seq(1L)
            .productId(1L)
            .quantity(2)
            .build();
        menuProductDao.save(menuProduct);

        Menu menu = new MenuBuilder()
            .id(1L)
            .menuGroupId(1L)
            .price(BigDecimal.valueOf(price))
            .menuProducts(new ArrayList<>())
            .name("뿌링클세트")
            .build();
        menu.getMenuProducts().add(menuProduct);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴를 등록 할 수 있다.")
    @Test
    void create (){
        MenuProduct menuProduct = new MenuProductBuilder()
            .seq(1L)
            .productId(1L)
            .quantity(2)
            .build();
        menuProductDao.save(menuProduct);

        Menu menu = new MenuBuilder()
            .id(1L)
            .menuGroupId(1L)
            .price(BigDecimal.valueOf(20000))
            .menuProducts(new ArrayList<>())
            .name("뿌링클세트")
            .build();

        menu.getMenuProducts().add(menuProduct);

        Menu savedMenu = menuBo.create(menu);

        MenuProduct savedMenuProduct1 = new MenuProductBuilder()
            .seq(1L)
            .menuId(1L)
            .productId(menuProduct.getProductId())
            .quantity(menuProduct.getQuantity())
            .build();

        Menu expectedMenu = new MenuBuilder()
            .id(1L)
            .menuGroupId(1L)
            .price(BigDecimal.valueOf(20000))
            .menuProducts(new ArrayList<>(Arrays.asList(savedMenuProduct1)))
            .name("뿌링클세트")
            .build();

        Assertions.assertThat(savedMenu).isEqualToComparingFieldByField(expectedMenu);
    }

}
