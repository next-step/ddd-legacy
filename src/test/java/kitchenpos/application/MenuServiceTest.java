package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any(String.class))).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // when
        Menu resultMenu = menuService.create(menu);

        // then
        assertThat(resultMenu.getId()).isNotNull();
        assertThat(resultMenu.getName()).isEqualTo(menu.getName());
        assertThat(resultMenu.getPrice()).isEqualTo(menu.getPrice());
        assertThat(resultMenu.getMenuGroup()).isEqualTo(menu.getMenuGroup());
        assertThat(resultMenu.getMenuProducts()).isEqualTo(menu.getMenuProducts());
        assertThat(resultMenu.isDisplayed()).isEqualTo(menu.isDisplayed());
    }

    @Test
    void changePrice() {
    }

    @Test
    void display() {
    }

    @Test
    void hide() {
    }

    @Test
    void findAll() {
    }
}