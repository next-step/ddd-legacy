package kitchenpos.application;



import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 테스트")
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroup menuGroup = new MenuGroup();

    @BeforeEach
    void setUp() {
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("세트1");
    }

    @DisplayName("메뉴의 가격은 0원 이상 이어야 한다.")
    @ParameterizedTest
    @MethodSource("bigDecimalZeroAndNull")
    void price_is_less_then_zero(BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu)
        );
    }

    @DisplayName("메뉴의 등록된 메뉴 그룹이 존재 해야 한다.")
    @Test
    void menu_has_menuGroup() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroup(menuGroup);

        //given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(null);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu)
        );
    }

    private static Stream<BigDecimal> bigDecimalZeroAndNull() { // argument source method
        return Stream.of(null, new BigDecimal(-1));
    }

}
