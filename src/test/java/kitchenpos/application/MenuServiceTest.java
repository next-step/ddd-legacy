package kitchenpos.application;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Name;
import kitchenpos.menu.menu.application.InMemoryMenuRepository;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.domain.Price;
import kitchenpos.menu.menu.domain.Quantity;
import kitchenpos.menu.menu.dto.MenuProductRequest;
import kitchenpos.menu.menu.dto.MenuRequest;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.menu.menugroup.infra.PurgomalumClient;
import kitchenpos.product.application.InMemoryProductRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.MenuFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴 서비스")
class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴 목록을 조회할 수 있다.")
    @Test
    void findMenus() {
        menuRepository.save(createMenu(createMenuGroup(UUID.randomUUID(), "메뉴그룹명"), createMenuProducts(new MenuProduct(new Product(new Name("상품명", false), new Price(BigDecimal.TEN)), new Quantity(1L))), new Price(BigDecimal.TEN)));
        assertThat(menuService.findAll()).hasSize(1);
    }

    @DisplayName("상품의 수량과 메뉴 상품의 수량은 다를 수 없다.")
    @Test
    void productSize() {
        MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        MenuProductRequest menuProductRequest = new MenuProductRequest();
        menuProductRequests.add(menuProductRequest);
        MenuRequest menuRequest = new MenuRequest(menuGroup.getId(), menuProductRequests);
        assertThatThrownBy(() -> menuService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품의 수량과 메뉴 상품의 수량은 다를 수 없다.");
    }
}


