package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import kitchenpos.support.MenuGroupBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class MenuGroupBoTest extends MockTest {
    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo sut;

    @Test
    @DisplayName("메뉴 그룹 생성")
    void createMenuGroup() {
        // given
        final MenuGroup menuGroup = MenuGroupBuilder.menuGroup().build();
        final MenuGroup savedMenuGroup = MenuGroupBuilder.menuGroup().withId(1).build();

        given(menuGroupDao.save(any(MenuGroup.class)))
                .willReturn(savedMenuGroup);

        // when
        sut.create(menuGroup);

        // then
        verify(menuGroupDao).save(any(MenuGroup.class));
    }

    @Test
    @DisplayName("메뉴 그룹 목록 조회")
    void getMenuGroups() {
        // given
        given(menuGroupDao.findAll()).willReturn(
                Collections.singletonList(MenuGroupBuilder.menuGroup().build())
        );

        // when
        sut.list();

        // then
        verify(menuGroupDao).findAll();
    }
}
