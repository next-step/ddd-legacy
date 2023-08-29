package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest extends AbstractApplicationServiceTest {

    @Mock
    private PurgomalumClient mockClient;

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    private MenuService service;

    @BeforeEach
    void setUp() {
        menuRepository = new MenuFakeRepository();
        menuGroupRepository = new MenuGroupFakeRepository();
        productRepository = new ProductFakeRepository();

        service = new MenuService(menuRepository, menuGroupRepository,
            productRepository, mockClient);
    }

    @DisplayName("메뉴를 모두 조회하여 반환한다.")
    @Test
    void findAll() {
        // given
        final Menu dummy1 = create("dummy1", BigDecimal.valueOf(1_000L));
        final Menu dummy2 = create("dummy2", BigDecimal.valueOf(2_000L));
        menuRepository.save(dummy1);
        menuRepository.save(dummy2);

        // when
        final List<Menu> actual = service.findAll();

        // then
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .contains(dummy1, dummy2);
    }

    private Menu create(final String name, final BigDecimal price) {
        return createMenuRequest(name, price, List.of(),
            createMenuGroupRequest("menuGroup"), false);
    }
}
