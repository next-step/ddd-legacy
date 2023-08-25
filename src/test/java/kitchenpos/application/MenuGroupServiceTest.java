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

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;
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
        //given
        given(menuGroupRepository.save(any())).willReturn(한식);
        //when
        MenuGroup returnMenuGroup = menuGroupService.create(한식);
        //then
        assertThat(returnMenuGroup).isEqualTo(한식);
    }

    @DisplayName("입력한 메뉴그룹명이 Null인경우 오류가 발생한다.")
    @Test
    void createFail() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuGroupService.create(new MenuGroup()));
    }

    @DisplayName("등록된 전체 메뉴그룹이 조회된다.")
    @Test
    void findAll() {
        //givne
        given(menuGroupRepository.findAll()).willReturn(List.of(한식, 양식));
        //when
        List<MenuGroup> menuGroups = menuGroupService.findAll();
        //then
        assertThat(menuGroups).containsOnly(한식, 양식);
    }
}