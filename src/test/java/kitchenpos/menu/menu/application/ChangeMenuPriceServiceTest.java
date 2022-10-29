package kitchenpos.menu.menu.application;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.dto.request.ChangeMenuPriceRequest;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("메뉴 가격 변경")
class ChangeMenuPriceServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ChangeMenuPriceService changeMenuPriceService;

    private Product product;
    private Menu menu;

    @BeforeEach
    void setUp() {
        changeMenuPriceService = new ChangeMenuPriceService(menuRepository);
        product = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
        MenuGroup 메뉴그룹 = 메뉴그룹생성();
        menu = 메뉴생성(메뉴그룹);
    }

    @DisplayName("메뉴 가격은 필수로 입력받는다.")
    @Test
    void name() {
        assertThatThrownBy(() -> changeMenuPriceService.changePrice(menu.getId(), new ChangeMenuPriceRequest(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    private Menu 메뉴생성(MenuGroup 메뉴그룹) {
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(new MenuProduct(product, new Quantity(1)));
        return menuRepository.save(new Menu(UUID.randomUUID(), new Name("메뉴명", false), 메뉴그룹, menuProducts, new Price(BigDecimal.ONE)));
    }

    private MenuGroup 메뉴그룹생성() {
        return menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴 그룹명", false)));
    }
}
