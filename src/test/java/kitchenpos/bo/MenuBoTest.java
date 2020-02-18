package kitchenpos.bo;

import kitchenpos.dao.*;
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
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MenuBoTest {

    private MenuDao menuDao = new TestMenuDao();
    private MenuGroupDao menuGroupDao = new TestMenuGroupDao();
    private MenuProductDao menuProductDao = new TestMenuProductDao();
    private ProductDao productDao = new TestProductDao();

    private MenuBo menuBo;

    private Random random = new Random();

    @BeforeEach
    void setUp() {
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);

        final MenuGroup defaultGroup = new MenuGroup() {{
           setId(1L);
           setName("기본 메뉴 그룹");
        }};
        menuGroupDao.save(defaultGroup);

        final Product friedChicken = new Product() {{
            setId(1L);
            setName("후라이드 치킨");
            setPrice(BigDecimal.valueOf(12_000L));
        }};
        productDao.save(friedChicken);

        final Product SeasonedChicken = new Product() {{
            setId(2L);
            setName("양념 치킨");
            setPrice(BigDecimal.valueOf(13_000L));
        }};
        productDao.save(SeasonedChicken);
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        final Menu expected = createMenu(BigDecimal.valueOf(24_000L));

        // when
        final Menu actual = menuBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
        assertThat(actual.getMenuGroupId()).isEqualTo(expected.getMenuGroupId());
        assertThat(actual.getMenuProducts().size()).isEqualTo(expected.getMenuProducts().size());
    }

    @DisplayName("이미 존재하는 메뉴 그룹에 속하지 않으면 메뉴를 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"2", "5000"})
    void invalidMenuGroup(final Long menuGroupId) {
        // given
        final Menu expected = createMenu(menuGroupId);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    @DisplayName("메뉴의 가격이 올바르지 않으면 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-10000"})
    void invalidPrice(final BigDecimal price) {
        // given
        final Menu expected = createMenu(price);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    @DisplayName("메뉴의 가격은 메뉴에 포함된 상품의 가격 합계보다 클 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"26000", "30000", "100000"})
    void tooExpensive(final BigDecimal price) {
        // given
        final Menu expected = createMenu(price);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    @DisplayName("메뉴 목록을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final Menu expected = createMenu();
        menuDao.save(expected);

        // when
        final List<Menu> actual = menuBo.list();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).contains(expected);
    }

    private Menu createMenu() {
        return createMenu(1L, BigDecimal.valueOf(24_000L));
    }

    private Menu createMenu(Long menuGroupId) {
        return createMenu(menuGroupId, BigDecimal.valueOf(24_000L));
    }

    private Menu createMenu(BigDecimal price) {
        return createMenu(1L, price);
    }

    private Menu createMenu(Long menuGroupId, BigDecimal price) {
        final Menu menu = new Menu();
        menu.setId(random.nextLong());
        menu.setMenuGroupId(menuGroupId);
        menu.setName("후라이드 반 양념 반");
        menu.setPrice(price);
        menu.setMenuProducts(Arrays.asList(
                new MenuProduct() {{
                    setSeq(1L);
                    setQuantity(1L);
                    setProductId(1L);
                    setMenuId(1L);
                }},
                new MenuProduct() {{
                    setSeq(2L);
                    setQuantity(1L);
                    setProductId(2L);
                    setMenuId(2L);
                }}
        ));

        return menu;
    }
}
