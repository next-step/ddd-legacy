package kitchenpos.integrationTest;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.fixtures.MenuFixture;
import kitchenpos.fixtures.MenuGroupFixture;
import kitchenpos.fixtures.MenuProductFixture;
import kitchenpos.fixtures.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class MenuServiceIntegrationTest {
    private MenuRepository menuRepository;
    private MenuService menuService;

    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Test
    void 메뉴를_등록_할_수_있다() {
        // Prepare data
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        productRepository.save(product);

        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());
        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(16000L), true, menuGroup, Collections.singletonList(menuProduct));

        // Mock PurgomalumClient
        given(purgomalumClient.containsProfanity(any())).willReturn(false);

        // Execute the create method
        Menu createdMenu = menuService.create(menu);

        // Assert the results
        assertThat(createdMenu.getId()).isNotNull();
        assertThat(createdMenu.getName()).isEqualTo("후라이드치킨세트");
        assertThat(createdMenu.getPrice()).isEqualTo(BigDecimal.valueOf(16000L));
        assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
        assertThat(createdMenu.getMenuProducts()).hasSize(1);
        assertThat(createdMenu.isDisplayed()).isTrue();
    }

    @Test
    void 가격이_음수면_메뉴_등록_실패() {
        Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(-16000L), true, menuGroup, Collections.singletonList(menuProduct));
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_메뉴그룹으로_메뉴_등록_실패() {
        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(16000L), true, MenuGroupFixture.create("치킨메뉴"), Collections.emptyList());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 메뉴_상품이_비어있으면_메뉴_등록_실패() {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        menuGroupRepository.save(menuGroup);

        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(16000L), true, menuGroup, Collections.emptyList());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품이_존재하지_않으면_메뉴_등록_실패() {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        menuGroupRepository.save(menuGroup);

        MenuProduct menuProduct = MenuProductFixture.create(ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L)), 1, UUID.randomUUID());
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(1);

        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(16000L), true, menuGroup, Collections.singletonList(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격이_합보다_크면_메뉴_등록_실패() {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        productRepository.save(product);

        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(30000L), true, menuGroup, Collections.singletonList(menuProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모든_메뉴를_조회할_수_있다() {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        productRepository.save(product);

        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

        Menu menu = MenuFixture.create("후라이드치킨세트", BigDecimal.valueOf(16000L), true, menuGroup, Collections.singletonList(menuProduct));

        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        menuService.create(menu);

        List<Menu> menus = menuService.findAll();
        assertThat(menus).hasSize(1);
        assertThat(menus.get(0).getName()).isEqualTo("후라이드치킨세트");
    }

    @Test
    void 욕설이_포함된_메뉴_등록_실패() {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨메뉴");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        productRepository.save(product);

        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

        Menu menu = MenuFixture.create("시발 후라이드치킨세트", BigDecimal.valueOf(16000L), true, menuGroup, Collections.singletonList(menuProduct));

        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
