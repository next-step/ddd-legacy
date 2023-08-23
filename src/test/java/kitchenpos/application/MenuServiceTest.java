package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.TEST_MENU;
import static kitchenpos.fixture.TestFixture.TEST_MENU_GROUP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @InjectMocks
    private MenuService menuService;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @Test
    @DisplayName("메뉴를 등록한다.")
    void createTest() {
        // given
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(TEST_MENU_GROUP()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(new Product()));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(true);
        given(menuRepository.save(any(Menu.class))).willReturn(TEST_MENU());


        // when
        menuService.create(any(Menu.class));

        // then
        then(menuGroupRepository).should(times(1)).findById(any(UUID.class));
        then(productRepository).should(times(1)).findAllByIdIn(any());
        then(productRepository).should(times(1)).findById(any(UUID.class));
        then(purgomalumClient).should(times(1)).containsProfanity(anyString());
        then(menuRepository).should(times(1)).save(any(Menu.class));
    }
}
