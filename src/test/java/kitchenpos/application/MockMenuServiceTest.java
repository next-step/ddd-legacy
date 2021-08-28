package kitchenpos.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@SpringBootTest
//@Transactional
@ExtendWith(MockitoExtension.class)
class MockMenuServiceTest {

    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private Menu menu;
    private MenuGroup menuGroup;
    private Product product;
    private MenuProduct menuProduct;

    @BeforeEach
    void setup() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        menu = MakeFixture.createMenuTestFixture();
        menuGroup = menu.getMenuGroup();
        product = menu.getMenuProducts().get(0).getProduct();
        menuProduct = menu.getMenuProducts().get(0);
    }

    @DisplayName("메뉴명에는 비속어는 사용할 수 없다.")
    @Test
    void menuCreateWithProfanity() {
        // given
//        menu = new Menu();
        menu.setName("test");
        given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.ofNullable(menuGroup));
        given(productRepository.findAllById(menu.getMenuProducts()
                .stream()
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList())))
                .willReturn(Collections.singletonList(menuProduct.getProduct()));
        given(productRepository.findById(product.getId())).willReturn(Optional.ofNullable(product));
        given(purgomalumClient.containsProfanity("test")).willReturn(true);
        // when
        assertThatThrownBy(() -> menuService.create(menu))
                // then
                .isInstanceOf(IllegalArgumentException.class);
        verify(purgomalumClient, times(1)).containsProfanity("test");
    }
}