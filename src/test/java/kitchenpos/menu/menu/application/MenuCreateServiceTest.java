package kitchenpos.menu.menu.application;

import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.dto.request.MenuProductRequest;
import kitchenpos.menu.menu.dto.request.MenuRequest;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("메뉴 서비스")
class MenuCreateServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurgomalumClient purgomalumClient;

    @Autowired
    private MenuCreateService menuCreateService;

    private MenuGroup menuGroup;
    private Product product;

    @BeforeEach
    void setUp() {
        menuCreateService = new MenuCreateService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        menuGroup = menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        product = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
    }

    @DisplayName("메뉴 목록을 조회할 수 있다.")
    @Test
    void findMenus() {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        menuProductRequests.add(new MenuProductRequest(product.getId(), 1));
        menuCreateService.create(new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.TEN, menuProductRequests));
        assertThat(menuCreateService.findAll()).hasSize(1);
    }

    @DisplayName("상품의 수량과 메뉴 상품의 수량은 다를 수 없다.")
    @Test
    void productSize() {
        MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        List<MenuProductRequest> menuProductRequests = getMenuProductRequests(UUID.randomUUID());
        MenuRequest menuRequest = new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.TEN, menuProductRequests);
        assertThatThrownBy(() -> menuCreateService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹에 속해 있다.")
    @Test
    void menuGroup() {
        List<MenuProductRequest> menuProductRequests = getMenuProductRequests(product.getId());
        MenuRequest menuRequest = new MenuRequest(UUID.randomUUID(), "메뉴명", BigDecimal.TEN, menuProductRequests);
        assertThatThrownBy(() -> menuCreateService.create(menuRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 가격이 0원보다 작을 수 없다.")
    @Test
    void price() {
        MenuRequest menuRequest = new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.valueOf(-1), getMenuProductRequests(product.getId()));
        assertThatThrownBy(() -> menuCreateService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴 상품 목록은 비어 있을 수 없다.")
    @Test
    void menuProducts() {
        MenuRequest menuRequest = new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.valueOf(2), null);
        assertThatThrownBy(() -> menuCreateService.create(menuRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 상품 목록은 비어있을 수 없다.");
    }

    @DisplayName("메뉴 가격을 필수로 입력받는다.")
    @Test
    void menuProduasdcts() {
        assertThatThrownBy(() -> menuCreateService.create(메뉴가격Null()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    private MenuRequest 메뉴가격Null() {
        return new MenuRequest(menuGroup.getId(), "메뉴명", null, 메뉴상품());
    }

    private List<MenuProductRequest> getMenuProductRequests(UUID product) {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        MenuProductRequest menuProductRequest = new MenuProductRequest(product, 1);
        menuProductRequests.add(menuProductRequest);
        return menuProductRequests;
    }

    private List<MenuProductRequest> 메뉴상품() {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        MenuProductRequest menuProductRequest = new MenuProductRequest(product.getId(), 1);
        menuProductRequests.add(menuProductRequest);
        return menuProductRequests;
    }
}


