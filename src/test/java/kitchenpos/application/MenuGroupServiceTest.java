package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.fake.FakeMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.*;
import static org.assertj.core.api.Assertions.*;

class MenuGroupServiceTest {
    FakeMenuGroupRepository menuGroupRepository;

    MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new FakeMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    void 메뉴_그룹을_생성한다() {
        final MenuGroup 주류 = 주류();

        final MenuGroup 결과 = menuGroupService.create(주류);

        assertThat(결과.getId()).isNotNull();
    }

    @Test
    void 메뉴_그룹을_조회한다() {
        menuGroupService.create(반반());

        final List<MenuGroup> 결과 = menuGroupService.findAll();

        assertThat(결과).hasSize(1);
    }

    @Test
    void 메뉴_그룹명이_비어있으면_실패한다() {
        final MenuGroup 비어있음 = 비어있음();
        assertThatThrownBy(() -> menuGroupService.create(비어있음))
                .isInstanceOf(IllegalArgumentException.class);
    }


}