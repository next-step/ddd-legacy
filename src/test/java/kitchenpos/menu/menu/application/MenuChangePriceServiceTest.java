package kitchenpos.menu.menu.application;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.MenuFixture;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.dto.request.ChangeMenuPriceRequest;
import kitchenpos.menu.menugroup.MenuGroupFixture;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("메뉴 가격 변경")
class MenuChangePriceServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuChangePriceService menuChangePriceService;

    private Product product;
    private Menu menu;

    @BeforeEach
    void setUp() {
        menuChangePriceService = new MenuChangePriceService(menuRepository);
        product = productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.menuGroup(UUID.randomUUID()));
        menu = menuRepository.save(MenuFixture.menu(menuGroup, MenuFixture.menuProducts(product.getId())));
    }

    @DisplayName("메뉴 가격은 필수로 입력받는다.")
    @Test
    void name() {
        assertThatThrownBy(() -> menuChangePriceService.changePrice(menu.getId(), new ChangeMenuPriceRequest(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    @DisplayName("메뉴 가격은 0원보다 크다.")
    @Test
    void nasdame() {
        assertThatThrownBy(() -> menuChangePriceService.changePrice(menu.getId(), new ChangeMenuPriceRequest(BigDecimal.valueOf(-1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.ONE);
        menuChangePriceService.changePrice(menu.getId(), new ChangeMenuPriceRequest(BigDecimal.valueOf(7)));
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(7));
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 합보다 크면 메뉴를 숨긴다.")
    @Test
    void changePricasde() {
        assertThat(menu.isDisplayed()).isTrue();
        menuChangePriceService.changePrice(menu.getId(), new ChangeMenuPriceRequest(BigDecimal.valueOf(20)));
        assertThat(menu.isDisplayed()).isFalse();
    }
}
