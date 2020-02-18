package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.MenuGroup;
import kitchenpos.support.MenuGroupBuilder;
import org.junit.jupiter.api.BeforeEach;
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
public class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    private MenuGroup request, result;

    @BeforeEach
    void setup (){
        request = new MenuGroupBuilder()
            .name("두마리메뉴")
            .build();

        result = new MenuGroupBuilder()
            .id(1L)
            .name("두마리메뉴")
            .build();

    }

    @DisplayName("메뉴 그룹을 저장한다.")
    @Test
    public void create() {
        given(menuGroupDao.save(request)).willReturn(result);
        MenuGroup saved = menuGroupBo.create(request);

        assertThat(request.getName()).isEqualTo(saved.getName());
    }

    @DisplayName("메뉴 그룹을 조회한다.")
    @Test
    public void list(){
        //given
        List<MenuGroup> expected = new ArrayList<>();
        expected.add(result);
        given(menuGroupDao.findAll()).willReturn(expected);

        //when
        List<MenuGroup> actual = menuGroupBo.list();

        //then
        assertThat(menuGroupBo.list()).isEqualTo(actual);
    }

}
