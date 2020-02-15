package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
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
        void create() {
            //given
            Menu actual = 치맥셋트();

            //when
            Menu expected = menuBo.create(치맥셋트());

            //then
            Assertions.assertAll(
                    () -> assertThat(expected).isNotNull(),
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                    () -> assertThat(actual.getMenuGroupId()).isEqualTo(expected.getMenuGroupId())
            );
        }

        @DisplayName("메뉴 가격은 0 원 이상의 숫자이다.")
        @ParameterizedTest
        @ValueSource(longs = {-1000, -2000})
        void create2(long price) {
            //given
            Menu actual = MenuBuilder
                    .aMenu()
                    .withId(3L)
                    .withMenuGroupId(1L)
                    .withMenuProducts(Arrays.asList(핫치킨(), 시원한생맥주()))
                    .withPrice(BigDecimal.valueOf(price))
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(actual));
        }


        @Test
        @DisplayName("메뉴 가격은 메뉴 제품의 가격 총합을 초과 할 수 없다.")
        void create3() {
            //given
            Menu actual = 치맥셋트();

            BigDecimal sumOfProductsPrice = actual.getMenuProducts().stream()
                    .map(i -> {
                        Product p = productDao.findById(i.getProductId())
                                .orElseGet(this::getZeroPriceProduct);

                        BigDecimal price = p.getPrice();
                        BigDecimal quantity = BigDecimal.valueOf(i.getQuantity());

                        return price.multiply(quantity);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(actual.getPrice())
                    .isLessThan(sumOfProductsPrice);
        }

        Product getZeroPriceProduct() {
            return ProductBuilder
                    .aProduct()
                    .withPrice(BigDecimal.valueOf(0))
                    .build();
        }
    }

    @Test
    @DisplayName("전체 메뉴 리스트를 조회 할 수 있다.")
    void listAll() {
        //given
        Menu createdMenu = 치맥셋트();
        List<Menu> actual = Arrays.asList(menuBo.create(치맥셋트()));

        //when
        List<Menu> expected = menuBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.stream().anyMatch(i -> {
                    Long expectedId = i.getId();
                    Long actualId = actual.get(0).getId();

                    return expectedId.equals(actualId);
                }))
        );
    }
}
