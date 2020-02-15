package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("사용자는 메뉴 그룹을 등록할 수 있고, 등록이 완료되면 등록된 메뉴 그룹 정보를 반환받아 확인할 수 있다")
    @Test
    void create() {
        //given
        MenuGroup item = new MenuGroup();
        item.setId(1L);
        item.setName("치킨");
        Mockito.when(menuGroupDao.save(item)).thenReturn(item);

        //when
        MenuGroup expected = menuGroupBo.create(item);

        //then
        assertThat(item).isEqualTo(expected);
    }

    @DisplayName("사용자는 등록된 모든 메뉴 그룹의 목록을 조회할 수 있다")
    @Test
    void list() {
        //given
        MenuGroup item1 = new MenuGroup();
        item1.setId(1L);
        item1.setName("치킨");

        MenuGroup item2 = new MenuGroup();
        item2.setId(2L);
        item2.setName("샐러드");

        MenuGroup item3 = new MenuGroup();
        item3.setId(2L);
        item3.setName("파스타");

        List<MenuGroup> menuGroups = Arrays.asList(item1, item2, item3);
        Mockito.when(menuGroupDao.findAll()).thenReturn(menuGroups);

        //when
        List<MenuGroup> actual = menuGroupBo.list();

        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual.get(0)).isEqualTo(item1);
        assertThat(actual.get(1)).isEqualTo(item2);
        assertThat(actual.get(2)).isEqualTo(item3);
    }
}
