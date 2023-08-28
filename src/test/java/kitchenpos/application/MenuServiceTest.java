package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixtures.메뉴_상품_등록;
import static kitchenpos.fixture.ProductFixtures.상품_등록;
import static kitchenpos.fixture.MenuFixtures.메뉴_등록;
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

//    private UUID id;
//
//    private Menu 등심돈까스_메뉴;
//    private MenuGroup 등심돈까스_메뉴_그룹;
//    private Product 등심돈까스_상품;
//    private Menu 등심_세트_메뉴;
//    private MenuProduct 등심_세트_메뉴_상품;
//
//
//    @BeforeEach
//    void setUp() {
//        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
//
//        id = UUID.randomUUID();
//        등심돈까스_메뉴 = 메뉴_등록();
//        등심돈까스_메뉴_그룹 = 메뉴_그룹_등록("등심돈까스_메뉴_그룹");
//        등심돈까스_상품 = 상품_등록("등심돈까스", 15000);
//        등심_세트_메뉴 = 메뉴_등록("등심돈까스_세트", 18000,
//                List.of(메뉴_상품_등록(상품_등록("등심돈까스", 15000), 1L),
//                        메뉴_상품_등록(상품_등록("음료", 3000), 1L)));
//        등심_세트_메뉴_상품 = 메뉴_상품_등록(상품_등록("등심돈까스", 15000), 1L);
//
//    }
//
//
//    @DisplayName("메뉴 전체를 조회한다.")
//    @Test
//    public void findAll() {
//        // given
//        Menu 등심돈까스 = 메뉴_등록();
//        Menu 안심돈까스_세트 = 메뉴_등록("안심돈까스_세트", 20000,
//                List.of(메뉴_상품_등록(상품_등록("안심돈까스", 18000), 1L),
//                        메뉴_상품_등록(상품_등록("음료", 3000), 1L)));
//        given(menuRepository.findAll()).willReturn(Arrays.asList(등심돈까스, 안심돈까스_세트));
//
//        // when
//        List<Menu> menus = menuService.findAll();
//
//        // then
//        assertAll(
//                () -> assertThat(menus.size()).isEqualTo(2)
//        );
//    }
//
//
//    @DisplayName("메뉴를 등록한다.")
//    @Test
//    public void create() {
//        // given
//        given(productRepository.findAllById(any())).willReturn(Arrays.asList(등심돈까스_상품));
//        given(productRepository.findById(any())).willReturn(Optional.of(등심돈까스_상품));
//        given(menuGroupRepository.findById(any())).willReturn(Optional.of(등심돈까스_메뉴_그룹));
//        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
//        given(menuRepository.save(any())).willReturn(등심_세트_메뉴);
//
//        // when
//        Menu createdMenu = menuService.create(등심_세트_메뉴);
//
//        // then
//        assertAll(
//                () -> assertThat(createdMenu.getName()).isEqualTo("등심돈까스"),
//                () -> assertThat(createdMenu.getPrice()).isEqualTo(BigDecimal.valueOf(15000)),
//                () -> assertThat(createdMenu.isDisplayed()).isEqualTo(true),
//                () -> assertThat(createdMenu.getMenuProducts().size()).isEqualTo(1),
//                () -> assertThat(createdMenu.getMenuProducts().get(0).getQuantity()).isEqualTo(1L),
//                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getPrice()).isEqualTo(BigDecimal.valueOf(15000)),
//                () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getName()).isEqualTo("등심돈까스")
//        );
//    }
}
