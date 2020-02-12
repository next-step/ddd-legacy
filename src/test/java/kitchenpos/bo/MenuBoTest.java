package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.mock.MenuBuilder;
import kitchenpos.mock.MenuProductBuilder;
import kitchenpos.mock.ProductBuilder;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

    @Mock
    private MenuDao menuDao;
    @Mock
    private MenuGroupDao menuGroupDao;
    @Mock
    private MenuProductDao menuProductDao;
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    @DisplayName("새로운 메뉴를 생성할 수 있다.")
    @Test
    void create() {
        // given

        Long menuId = 1L;
        Long product1Id = 1L;
        Long product2Id = 2L;

        Product product1 = ProductBuilder.mock()
                .withId(product1Id)
                .withName("제품1")
                .withPrice(BigDecimal.valueOf(1000))
                .build();

        Product product2 = ProductBuilder.mock()
                .withId(product2Id)
                .withName("제품2")
                .withPrice(BigDecimal.valueOf(500))
                .build();

        MenuProduct menuProduct1 = MenuProductBuilder.mock()
                .withMenuId(menuId)
                .withProductId(product1Id)
                .withQuantity(1)
                .build();

        MenuProduct menuProduct2 = MenuProductBuilder.mock()
                .withMenuId(menuId)
                .withProductId(product2Id)
                .withQuantity(2)
                .build();

        Menu newMenu = MenuBuilder.mock()
                .withName("메뉴1")
                .withPrice(BigDecimal.valueOf(1000))
                .withMenuGroupId(1L)
                .withMenuProducts(new ArrayList(Arrays.asList(menuProduct1, menuProduct2)))
                .build();

        given(menuGroupDao.existsById(any())).willReturn(true);
        given(productDao.findById(product1.getId())).willReturn(Optional.of(product1));
        given(productDao.findById(product2.getId())).willReturn(Optional.of(product2));
        given(menuDao.save(newMenu)).willAnswer((invocation) -> {
            newMenu.setId(1L);
            return newMenu;
        });
        given(menuProductDao.save(menuProduct1)).willAnswer(invocation -> {
            menuProduct1.setSeq(1L);
            return menuProduct1;
        });
        given(menuProductDao.save(menuProduct2)).willAnswer(invocation -> {
            menuProduct2.setSeq(2L);
            return menuProduct2;
        });

        // when
        Menu result = menuBo.create(newMenu);

        // then
        assertThat(result.getId()).isEqualTo(newMenu.getId());
        assertThat(result.getName()).isEqualTo(newMenu.getName());
        assertThat(result.getPrice()).isEqualTo(newMenu.getPrice());
        assertThat(result.getMenuGroupId()).isEqualTo(newMenu.getMenuGroupId());
        assertThat(result.getMenuProducts()).containsExactlyInAnyOrder(menuProduct1, menuProduct2);
    }

    @DisplayName("메뉴 생성 시, 메뉴는 1개의 메뉴그룹에 속해야한다.")
    @ParameterizedTest
    @MethodSource(value = "provideInvalidMenuGroupId")
    void shouldBeInMenuGroup(Long menuGroupId) {
        // given
        MenuProductBuilder menuProductBuilder = MenuProductBuilder.mock()
                .withProductId(1L)
                .withQuantity(1);
        Menu newMenu = MenuBuilder.mock()
                .withName("메뉴1")
                .withPrice(BigDecimal.valueOf(1000))
                .withMenuGroupId(menuGroupId)
                .withMenuProducts(new ArrayList(Arrays.asList(menuProductBuilder.build())))
                .build();

        given(menuGroupDao.existsById(any())).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream provideInvalidMenuGroupId() {
        return Stream.of(
                null,
                100L
        );
    }

    @DisplayName("메뉴는 1개 이상의 제품으로 구성된다.")
    @ParameterizedTest
    @MethodSource(value = "provideInvalidMenuProducts")
    void shouldIncludeAtLeastOneProduct(List<MenuProduct> invalidMenuProducts) {
        // given
        Menu newMenu = MenuBuilder.mock()
                .withName("메뉴1")
                .withPrice(BigDecimal.valueOf(1000))
                .withMenuGroupId(1L)
                .withMenuProducts(invalidMenuProducts)
                .build();

        given(menuGroupDao.existsById(any())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream provideInvalidMenuProducts() {
        return Stream.of(
                new ArrayList<>(),
                Collections.singletonList(new MenuProduct())
        );
    }

    @DisplayName("메뉴 가격은 0원 이상이다.")
    @ParameterizedTest
    @MethodSource(value = "provideInvalidPrice")
    void priceShouldBeOver0(BigDecimal invalidPrice) {
        // given
        MenuProductBuilder menuProductBuilder = MenuProductBuilder.mock()
                .withProductId(1L)
                .withQuantity(1);
        Menu newMenu = MenuBuilder.mock()
                .withName("메뉴1")
                .withPrice(invalidPrice)
                .withMenuGroupId(1L)
                .withMenuProducts(Collections.singletonList(menuProductBuilder.build()))
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream provideInvalidPrice() {
        return Stream.of(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(-100),
                BigDecimal.valueOf(-10),
                BigDecimal.valueOf(-1)
        );
    }

    @DisplayName("메뉴 가격은 구성된 메뉴제품들의 가격 총합을 초과할 수 없다.")
    @ParameterizedTest
    @MethodSource(value = "provideInvalidTotalPrice")
    void priceShouldNotOverSumOfProductPrices(BigDecimal invalidPrice) {
        // given
        Long menuId = 1L;
        Long product1Id = 1L;
        Long product2Id = 2L;

        Product product1 = ProductBuilder.mock()
                .withId(product1Id)
                .withName("제품1")
                .withPrice(BigDecimal.valueOf(1000))
                .build();

        Product product2 = ProductBuilder.mock()
                .withId(product2Id)
                .withName("제품2")
                .withPrice(BigDecimal.valueOf(500))
                .build();

        MenuProduct menuProduct1 = MenuProductBuilder.mock()
                .withMenuId(menuId)
                .withProductId(product1Id)
                .withQuantity(1)
                .build();

        MenuProduct menuProduct2 = MenuProductBuilder.mock()
                .withMenuId(menuId)
                .withProductId(product2Id)
                .withQuantity(2)
                .build();

        Menu newMenu = MenuBuilder.mock()
                .withName("메뉴1")
                .withPrice(invalidPrice)
                .withMenuGroupId(1L)
                .withMenuProducts(new ArrayList(Arrays.asList(menuProduct1, menuProduct2)))
                .build();


        given(menuGroupDao.existsById(any())).willReturn(true);
        given(productDao.findById(product1Id)).willReturn(Optional.of(product1));
        given(productDao.findById(product2Id)).willReturn(Optional.of(product2));

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream provideInvalidTotalPrice() {
        return Stream.of(
                BigDecimal.valueOf(2001),
                BigDecimal.valueOf(10000)
        );
    }

    @DisplayName("전체 메뉴 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        Long menuId = 1L;
        MenuProduct menuProduct1 = MenuProductBuilder.mock()
                .withSeq(1L)
                .withMenuId(menuId)
                .withProductId(1L)
                .withQuantity(1)
                .build();
        MenuProduct menuProduct2 = MenuProductBuilder.mock()
                .withSeq(2L)
                .withMenuId(menuId)
                .withProductId(2L)
                .withQuantity(2)
                .build();
        Menu menu = MenuBuilder.mock()
                .withId(menuId)
                .withName("메뉴1")
                .withPrice(BigDecimal.valueOf(1000))
                .withMenuGroupId(1L)
                .withMenuProducts(new ArrayList(Arrays.asList(
                        menuProduct1, menuProduct2
                )))
                .build();

        given(menuDao.findAll()).willReturn(Collections.singletonList(menu));
        given(menuProductDao.findAllByMenuId(any(Long.class))).willReturn(new ArrayList<>(Arrays.asList(menuProduct1, menuProduct2)));

        // when
        final List<Menu> result = menuBo.list();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(menu.getId());
        assertThat(result.get(0).getName()).isEqualTo(menu.getName());
        assertThat(result.get(0).getMenuProducts()).containsExactlyInAnyOrder(menuProduct1, menuProduct2);
    }
}
