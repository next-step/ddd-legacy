package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.MenuGroup;
import org.assertj.core.api.Assertions;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository.deleteAll();
    }

    @DisplayName(value = "메뉴 그룹을 생성 할 수 있다.")
    @Test
    @Transactional
    void create() {
        //given
        String name = "한마리메뉴";
        MenuGroup request = new MenuGroup();
        request.setName(name);

        //when
        MenuGroup result = menuGroupService.create(request);

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(name);

    }

    @DisplayName(value = "메뉴 그룹 생성 시 그룹 명이 null일 경우 IllegalArgumentException 예외 처리를 한다.")
    @Test
    @Transactional
    void createMenuGroupWithNullName() {
        //given
        MenuGroup request = new MenuGroup();
        request.setName(null);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @DisplayName(value = "메뉴 그룹 생성 시 그룹 명이 빈 값일 경우 IllegalArgumentException 예외 처리를 한다.")
    @Test
    @Transactional
    void createMenuGroupWithEmptyName() {
        //given
        MenuGroup request = new MenuGroup();
        request.setName("");

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @DisplayName(value = "모든 메뉴 그룹을 조회 할 수 있다.")
    @Test
    @Transactional
    void findAll() {
        //given
        String firstName = "한마리메뉴";
        MenuGroup firstRequest = new MenuGroup();
        firstRequest.setName(firstName);

        String secondName = "두마리메뉴";
        MenuGroup secondRequest = new MenuGroup();
        secondRequest.setName(secondName);

        MenuGroup firstGroup = menuGroupService.create(firstRequest);
        MenuGroup secondGroup = menuGroupService.create(secondRequest);

        //when
        List<MenuGroup> result = menuGroupService.findAll();

        //then
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).extracting(MenuGroup::getName).containsExactly("한마리메뉴", "두마리메뉴");
    }
}