package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.NAME_순살치킨;
import static kitchenpos.fixture.MenuFixture.PRICE_32000;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuFixture.menuResponse;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupResponse;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.NAME_강정치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_17000;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 서비스 테스트")
@ApplicationMockTest
class MenuServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    private MenuGroup MENU_GROUP_추천메뉴;
    private Product PRODUCT_강정치킨;
    private Product PRODUCT_후라이드치킨;
    private MenuProduct 강정치킨_1개;
    private MenuProduct 후라이드치킨_1개;
    private UUID ID_MENU_GOURP_추천메뉴;

    @BeforeEach
    void setUp() {
        MENU_GROUP_추천메뉴 = menuGroupResponse("추천메뉴");
        PRODUCT_강정치킨 = productResponse(NAME_강정치킨, PRICE_17000);
        PRODUCT_후라이드치킨 = productResponse(NAME_후라이드치킨, PRICE_18000);
        강정치킨_1개 = menuProductResponse(1L, PRODUCT_강정치킨, 1);
        후라이드치킨_1개 = menuProductResponse(2L, PRODUCT_후라이드치킨, 1);
        ID_MENU_GOURP_추천메뉴 = MENU_GROUP_추천메뉴.getId();
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void creatMenu() {
        // given
        Menu request = buildCreateRequest();
        commonStubForCreateMenu();
        stubMenuRepositorySave();

        // when
        Menu result = menuService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(NAME_순살치킨),
                () -> assertThat(result.getPrice()).isEqualTo(PRICE_32000),
                () -> assertThat(result.isDisplayed()).isTrue(),
                () -> assertThat(result.getMenuGroupId()).isEqualTo(ID_MENU_GOURP_추천메뉴),
                () -> assertThat(result.getMenuProducts()).hasSize(2)
                        .containsExactly(강정치킨_1개, 후라이드치킨_1개)
        );
    }
}
