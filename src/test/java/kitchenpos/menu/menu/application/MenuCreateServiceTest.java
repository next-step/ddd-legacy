package kitchenpos.menu.menu.application;

import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.domain.MenuRepository;
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
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.menu.menu.MenuRequestFixture.*;
import static org.assertj.core.api.Assertions.*;

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
        menuCreateService.create(메뉴(menuGroup, product));
        assertThat(menuCreateService.findAll()).hasSize(1);
    }

    @DisplayName("상품의 수량과 메뉴 상품의 수량은 다를 수 없다.")
    @Test
    void productSize() {
        assertThatThrownBy(() -> menuCreateService.create(상품수량_메뉴상품_수량_다름(menuGroup, product)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹에 속해 있다.")
    @Test
    void menuGroup() {
        assertThatThrownBy(() -> menuCreateService.create(다른메뉴그룹ID(menuGroup, product)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 가격이 0원보다 작을 수 없다.")
    @Test
    void 메뉴가격_양수() {
        assertThatThrownBy(() -> menuCreateService.create(메뉴가격_음수(menuGroup, product)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴 상품 목록은 비어 있을 수 없다.")
    @Test
    void 메뉴생성_상품목록_필수() {
        assertThatThrownBy(() -> menuCreateService.create(메뉴상품_NULL(menuGroup, product)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 상품 목록은 비어있을 수 없다.");
    }

    @DisplayName("메뉴 가격을 필수로 입력받는다.")
    @Test
    void 메뉴생성_가격필수() {
        assertThatThrownBy(() -> menuCreateService.create(메뉴가격_NULL(menuGroup, product)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void 메뉴생성() {
        assertThatNoException().isThrownBy(() -> menuCreateService.create(메뉴(menuGroup, product)));
    }
}


