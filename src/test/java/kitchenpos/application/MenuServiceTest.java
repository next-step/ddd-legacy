package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.fixture.MenuFixtures.메뉴_상품_등록;
import static kitchenpos.fixture.MenuFixtures.메뉴_상품_등록_요청;
import static kitchenpos.fixture.ProductFixtures.상품_등록;
import static kitchenpos.fixture.MenuFixtures.메뉴_등록;
import static kitchenpos.fixture.MenuFixtures.메뉴_등록_요청;
import static kitchenpos.fixture.MenuGroupFixtures.메뉴_그룹_등록;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;

@DisplayName("메뉴")
public class MenuServiceTest extends ApplicationTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    private UUID id;
    private Product 등심돈까스_상품;
    private MenuGroup 등심돈까스_메뉴_그룹;
    private Menu 등심돈까스_메뉴_등록;
    private Menu 등심_세트_메뉴;
    private MenuProduct 등심_세트_메뉴_상품;


    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

        id = UUID.randomUUID();
        등심돈까스_상품 = 상품_등록("등심돈까스", 15000);
        등심돈까스_메뉴_그룹 = 메뉴_그룹_등록("등심세트");

        등심돈까스_메뉴_등록 = 메뉴_등록_요청("등심돈까스", 15000, true, List.of(메뉴_상품_등록_요청(1L)));
        등심_세트_메뉴 = 메뉴_등록("등심돈까스", 15000, true,
                List.of(메뉴_상품_등록(등심돈까스_상품, 1L)));
        등심_세트_메뉴_상품 = 메뉴_상품_등록(상품_등록("등심돈까스", 15000), 1L);


    }


    @DisplayName("메뉴 전체를 조회한다.")
    @Test
    public void findAll() {
        // given
        Menu 등심돈까스 = 메뉴_등록();
        Menu 안심돈까스_세트 = 메뉴_등록("안심돈까스_세트", 20000,
                List.of(메뉴_상품_등록(상품_등록("안심돈까스", 18000), 1L),
                        메뉴_상품_등록(상품_등록("음료", 3000), 1L)));
        given(menuRepository.findAll()).willReturn(Arrays.asList(등심돈까스, 안심돈까스_세트));

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertAll(
                () -> assertThat(menus.size()).isEqualTo(2)
        );
    }


    @DisplayName("메뉴를 등록한다.")
    @Test
    public void create() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(등심돈까스_메뉴_그룹));
        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(등심돈까스_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(등심돈까스_상품));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(menuRepository.save(any())).willReturn(등심_세트_메뉴);


        // when
        Menu createdMenu = menuService.create(등심돈까스_메뉴_등록);

        // then
        assertAll(
                () -> assertThat(createdMenu.getName()).isEqualTo("등심돈까스"),
                () -> assertThat(createdMenu.getPrice()).isEqualTo(BigDecimal.valueOf(15000)),
                () -> assertThat(createdMenu.isDisplayed()).isEqualTo(true),
                () -> assertThat(createdMenu.getMenuProducts().size()).isEqualTo(1),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getQuantity()).isEqualTo(1L),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getPrice()).isEqualTo(BigDecimal.valueOf(15000)),
                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getName()).isEqualTo("등심돈까스")
        );
    }


    @DisplayName("메뉴 등록시 상품 가격(음수)을 체크 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-10000})
    public void createCheckPrice(int price) {
        // given
        Menu menu = 메뉴_등록("등심돈까스", price, null);

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("메뉴 등록시 메뉴그룹 포함 여부 체크 한다.")
    @Test
    public void createCheckMenuGroup() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.create(등심돈까스_메뉴_등록))
                .isInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("메뉴 등록시 메뉴 이름(비속어)을 체크 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck"})
    public void createWithoutBadWord(String name) {
        // given
        Menu menu = 메뉴_등록_요청(name, 15000, true, Arrays.asList(등심_세트_메뉴_상품));

        given(productRepository.findAllByIdIn(any())).willReturn(Arrays.asList(등심돈까스_상품));
        given(productRepository.findById(any())).willReturn(Optional.of(등심돈까스_상품));
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(등심돈까스_메뉴_그룹));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
