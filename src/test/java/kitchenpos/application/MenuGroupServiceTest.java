package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuGroupService menuGroupService;
    private MenuGroup 한식 = new MenuGroup(UUID.randomUUID(), "한식");
    private MenuGroup 양식 = new MenuGroup(UUID.randomUUID(), "양식");

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("입력한 메뉴그룹명으로 return 된다.")
    @Test
    void create() {
        given(menuGroupRepository.save(any())).willReturn(한식);

        MenuGroup returnMenuGroup = menuGroupService.create(한식);

        assertThat(returnMenuGroup).isEqualTo(한식);
    }

    @DisplayName("등록된 전체 메뉴그룹이 조회된다.")
    @Test
    void findAll() {
        given(menuGroupRepository.findAll())
            .willReturn(List.of(한식, 양식));

        List<MenuGroup> menuGroups = menuGroupService.findAll();
        
        assertThat(menuGroups).containsOnly(한식, 양식);
    }
}