package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kitchenpos.objectmother.MenuGroupMaker.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹 생성 후 메뉴그룹조회시 추가한 메뉴그룹이 조회되야 한다.")
    @Test
    void 메뉴그룹생성() {
        // when
        MenuGroup menuGroup = menuGroupService.create(메뉴그룹_1);

        // then
        assertThat(menuGroup).isNotNull();
        assertThat(menuGroup.getName()).isEqualTo(메뉴그룹_1.getName());
    }

    @DisplayName("메뉴그룹 생성 시 이름이 존재하지 않으면 에러를 던진다.")
    @Test
    void 메뉴그룹생성_실패_이름미존재() {
        // when then
        assertThatThrownBy(() -> menuGroupService.create(메뉴그룹_이름없음))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹 전체조회시 지금까지 등록된 메뉴그룹이 전부 조회되야한다.")
    @Test
    void 메뉴그룹전체조회() {
        // given
        menuGroupService.create(메뉴그룹_1);
        menuGroupService.create(메뉴그룹_2);

        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertThat(menuGroups)
                .hasSize(2)
                .extracting(MenuGroup::getName)
                .containsExactlyInAnyOrder(메뉴그룹_1.getName(), 메뉴그룹_2.getName());
    }
}