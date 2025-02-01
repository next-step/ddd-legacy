package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.MenuGroupFixture.*;
import static kitchenpos.fixture.MenuProductFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Autowired
    @MockBean
    MenuRepository menuRepository;

    @Autowired
    @MockBean
    MenuGroupRepository menuGroupRepository;

    @Autowired
    @MockBean
    ProductRepository productRepository;

    @Autowired
    @MockBean
    PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    @DisplayName("메뉴를 등록할 수 있습니다.")
    @Test
    void crate() {
        final Menu menu = MenuFixture.menu();

        menuService.create(menu);
    }

    @DisplayName("메뉴 가격은 0원 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"-1", "-1000", "-100000"})
    void createWithNegativePrice(String price) {
        final Menu menu = MenuFixture.menu(null, "양념 후라이드 세트", new BigDecimal(price),
                menuGroup(), List.of(menuProduct()), true
        );
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹이 존재하지 않으면 메뉴를 등록할 수 없습니다.")
    @Test
    void createWithNotExistsMenuGroup() {
        final Menu menu = MenuFixture.menu(null, "양념 후라이드 세트", new BigDecimal("30000"),
                menuGroup(), List.of(menuProduct()), true
        );
        when(menuGroupRepository.findById(menu.getMenuGroupId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }
}
