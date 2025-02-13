package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);

    MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @Test
    @DisplayName("메뉴 그룹 생성 - 이름이 없을 경우 예외를 발생 시킨다.")
    void createWithoutName() {
        var request = new MenuGroup();
        assertThatThrownBy(() -> menuGroupService.create(request)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹 생성")
    void create() {
        // given
        var request = new MenuGroup();
        request.setName("치킨 까스");
        when(menuGroupRepository.save(any())).thenReturn(request);

        // when
        var response = menuGroupService.create(request);

        // then
        assertThat(response).isNotNull();
    }

}
