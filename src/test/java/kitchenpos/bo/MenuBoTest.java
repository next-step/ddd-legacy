package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuBuilder;
import kitchenpos.model.MenuProduct;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static kitchenpos.bo.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class MenuBoTest {

    private final MenuDao menuDao = new TestMenuDao();
    private final MenuGroupDao menuGroupDao = new TestMenuGroupDao();
    private final MenuProductDao menuProductDao = new TestMenuProductDao();
    private final ProductDao productDao = new TestProductDao();

    private MenuBo menuBo;

    @BeforeEach
    void setUp() {
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);

        //사전에 DB에 Insert 되어 있어야 하는 정보
        menuGroupDao.save(야식());
        productDao.save(치킨());
        productDao.save(맥주());
        menuProductDao.save(핫치킨());
        menuProductDao.save(시원한생맥주());
    }

    @Nested
    @DisplayName("메뉴 등록 테스트")
    class create {
        @Test
        @DisplayName("새로운 메뉴를 등록 할 수 있다.")
        void create1() {
            //given
            Menu expected = 치맥셋트();

            //when
            Menu actual = menuBo.create(expected);

            //then
            assertThat(actual).isNotNull();
            Assertions.assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                    () -> assertThat(actual.getMenuGroupId()).isEqualTo(expected.getMenuGroupId()),
                    () -> assertThat(actual.getMenuProducts()).containsExactlyInAnyOrderElementsOf(expected.getMenuProducts())
            );
        }

        @DisplayName("메뉴 가격은 0 원 이상의 숫자이다.")
        @ParameterizedTest
        @ValueSource(longs = {-1000, -2000})
        void create2(long price) {
            //given
            Menu expected = MenuBuilder
                    .aMenu()
                    .withId(3L)
                    .withMenuGroupId(1L)
                    .withMenuProducts(Arrays.asList(핫치킨(), 시원한생맥주()))
                    .withPrice(BigDecimal.valueOf(price))
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(expected));
        }

        @Test
        @DisplayName("메뉴 가격은 메뉴 제품의 가격 총합을 초과할 수 없다.")
        void create3(){
            //given
            MenuProduct product = 핫치킨();
            BigDecimal productPrice = productDao.findById(product.getProductId())
                                        .orElseThrow(IllegalArgumentException::new)
                                        .getPrice();
            BigDecimal menuPrice = productPrice.add(BigDecimal.TEN); // 메뉴 가격이 제품의 가격을 초과

            Menu expected = MenuBuilder
                    .aMenu()
                    .withId(3L)
                    .withMenuGroupId(1L)
                    .withMenuProducts(Collections.singletonList(product))
                    .withPrice(menuPrice)
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(()-> menuBo.create(expected));
        }
    }

    @Test
    @DisplayName("메뉴 리스트를 조회 할 수 있다.")
    void listAll() {
        //given
        List<Menu> expected = Collections.singletonList(menuBo.create(치맥셋트()));

        //when
        List<Menu> actual = menuBo.list();

        //then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
