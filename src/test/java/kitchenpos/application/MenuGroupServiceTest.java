package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    MenuGroupService menuGroupService;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Test
    void 메뉴_그룹을_생성한다() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴 그룹");
        given(menuGroupRepository.save(any())).willReturn(menuGroup);

        MenuGroup actual = menuGroupService.create(menuGroup);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 메뉴_그룹_목록을_조회한다() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴 그룹");
        given(menuGroupRepository.findAll()).willReturn(Arrays.asList(menuGroup));

        assertThat(menuGroupService.findAll()).hasSize(1);
    }
}
