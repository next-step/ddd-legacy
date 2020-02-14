package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuGroupTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        this.menuGroup = new MenuGroup(1L, "추천메뉴");
    }

    @Test
    @DisplayName("메뉴 그룹(카테고리) 등록")
    void createMenuGroup() {
        // give
        given(menuGroupDao.save(menuGroup))
                .willReturn(menuGroup);
        // when
        MenuGroup menuGroupExpected = menuGroupBo.create(menuGroup);
        MenuGroup menuGroupActual = menuGroup;
        // then
        assertThat(menuGroupExpected.getId()).isEqualTo(menuGroupActual.getId());

    }
}
