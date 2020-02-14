package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTests {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private static MenuGroup mockMenuGroup = new MenuGroup();
    private static final List<MenuGroup> mockMenuGroups = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        mockMenuGroup.setId(1L);
        mockMenuGroup.setName("test");

        mockMenuGroups.add(mockMenuGroup);
    }

    @DisplayName("메뉴 그룹 전체 목록 조회")
    @Test
    public void getMenuGroupList() {
        given(menuGroupDao.findAll()).willReturn(mockMenuGroups);

        List<MenuGroup> menuGroups = menuGroupBo.list();

        assertThat(menuGroups.get(0).getName()).isEqualTo("test");
    }

    @DisplayName("정상적인 값들로 메뉴 그룹 생성 시도 시 성공")
    @Test
    public void createMenuGroupSuccess() {
        given(menuGroupDao.save(mockMenuGroup)).willReturn(mockMenuGroup);

        MenuGroup saved = menuGroupBo.create(mockMenuGroup);

        assertThat(saved.getName()).isEqualTo("test");
    }
}
