package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    @InjectMocks
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp(){
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    @Test
    void create() {
        //given
        final MenuGroup request = createMenuGroupRequest();

        //when
        final MenuGroup actual = menuGroupService.create(request);

        //then
        assertThat(actual.getName()).isEqualTo(request.getName());

    }

    @DisplayName("메뉴 그룹명이 비어있으면 예외가 발생한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create2(final String text) {
        //given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(text);

        //when, then
        assertThatIllegalArgumentException().isThrownBy(() -> menuGroupService.create(menuGroup));

    }


    @DisplayName("모든 메뉴 그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        //given
        MenuGroup request = createMenuGroupRequest("한마리메뉴");
        MenuGroup request2 = createMenuGroupRequest("두마리메뉴");

        menuGroupRepository.save(request);
        menuGroupRepository.save(request2);

        //when
        List<MenuGroup> actuals = menuGroupRepository.findAll();

        //then
        assertThat(actuals).hasSize(2);

    }

    private static MenuGroup createMenuGroupRequest() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한마리메뉴");
        return createMenuGroupRequest(menuGroup.getName());
    }

    private static MenuGroup createMenuGroupRequest(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }


}