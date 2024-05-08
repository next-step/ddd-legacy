package kitchenpos.menu.service;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.MenuTestHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@DisplayName("메뉴 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @InjectMocks
    private MenuService menuService;


    @Test
    @DisplayName("새로운 메뉴를 추가할 수 있다.")
    void create() {
        Menu 메뉴 = MenuTestHelper.메뉴;

        Mockito.when(menuGroupRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(MenuTestHelper.extractMenuGroupFrom(메뉴)));
        Mockito.when(productRepository.findAllByIdIn(Mockito.any()))
                .thenReturn(MenuTestHelper.extractProductsFrom(메뉴));
        Mockito.when(productRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(MenuTestHelper.extractProductFrom(메뉴)));
        Mockito.when(purgomalumClient.containsProfanity(Mockito.any()))
                .thenReturn(false);
        Mockito.when(menuRepository.save(Mockito.any()))
                .thenReturn(메뉴);

        Menu result = menuService.create(메뉴);

        Assertions.assertThat(result.getName()).isEqualTo(메뉴.getName());
    }
}
