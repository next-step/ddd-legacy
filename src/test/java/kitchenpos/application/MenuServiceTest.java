package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.config.FakePurgomalumClient;
import kitchenpos.config.InMemoryMenuGroupRepository;
import kitchenpos.config.InMemoryMenuRepository;
import kitchenpos.config.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuServiceTest {

    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private ProductRepository productRepository = new InMemoryProductRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private MenuService menuService;

    private MenuGroupService menuGroupService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
                purgomalumClient);
    }

    @Test
    void 상품들을_조합하여_메뉴를_생성한다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu createRequest = MenuFixture.createRequest(30_000L, menuGroup, product, 2);

        Menu actual = menuService.create(createRequest);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 메뉴에_상품이_1개_이상_존재하지_않으면_예외를_던진다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu createRequest = MenuFixture.createRequest(30_000L, menuGroup, product, 0);

        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(createRequest));
    }


    @Test
    void 메뉴이름에_욕설이나_부적절한_언어를_사용하면_예외를_던진다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu createRequest = MenuFixture.createRequest("욕설", 30_000L, menuGroup, product, 2);

        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(createRequest));
    }

    @Test
    void 메뉴는_메뉴그룹에_속하지않으면_예외를_던진다() {
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu createRequest = MenuFixture.createRequest(30_000L, null, product, 2);

        assertThatThrownBy(() -> menuService.create(createRequest)).isInstanceOf(
                NoSuchElementException.class);
    }

    @Test
    void 메뉴가격은_0보다_작으면_예외를_던진다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu createRequest = MenuFixture.createRequest(-20_000L, menuGroup, product, 2);

        assertThatThrownBy(() -> menuService.create(createRequest)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void 메뉴가격은_상품가격x상품갯수의_총합을_넘으면_예외를_던진다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu createRequest = MenuFixture.createRequest(50_000L, menuGroup, product, 2);

        assertThatThrownBy(() -> menuService.create(createRequest)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void 메뉴가격을_수정한다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu saved = menuService.create(MenuFixture.createRequest(30_000L, menuGroup, product, 2));

        Menu actual = menuService.changePrice(saved.getId(),
                MenuFixture.changePriceRequest(40_000L));

        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(40_000L));
    }

    @Test
    void 메뉴가격_수정시_0보다_작으면_예외를_던진다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu saved = menuService.create(MenuFixture.createRequest(30_000L, menuGroup, product, 2));

        assertThatThrownBy(() -> menuService.changePrice(saved.getId(),
                MenuFixture.changePriceRequest(-40_000L))).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void 메뉴가격_수정시_상품가격x상품갯수의_총합을_넘으면_예외를_던진다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu saved = menuService.create(MenuFixture.createRequest(30_000L, menuGroup, product, 2));

        assertThatThrownBy(() -> menuService.changePrice(saved.getId(),
                MenuFixture.changePriceRequest(50_000L))).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_손님들에게_노출한다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu saved = menuService.create(MenuFixture.createRequest(30_000L, menuGroup, product, 2));

        Menu actual = menuService.display(saved.getId());

        assertThat(actual.isDisplayed()).isTrue();
    }

    @Test
    void 메뉴를_손님들에게_숨긴다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        Menu saved = menuService.create(MenuFixture.createRequest(30_000L, menuGroup, product, 2));

        Menu actual = menuService.hide(saved.getId());

        assertThat(actual.isDisplayed()).isFalse();
    }

    @Test
    void 모든_메뉴_목록을_볼_수_있다() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product product = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        menuService.create(MenuFixture.createRequest("후라이드2마리", 30_000L, menuGroup, product, 2));
        menuService.create(MenuFixture.createRequest("후라이드1마리", 20_000L, menuGroup, product, 1));

        List<Menu> actual = menuService.findAll();

        assertThat(actual).hasSize(2);
    }
}