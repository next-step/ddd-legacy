package kitchenpos.bo;

import kitchenpos.dao.MenuGroupDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MenuGroupTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuBo menuBo;

    @Test
    @DisplayName("메뉴 그룹(카테고리) 등록")
    void createMenuGroup() {

    }
}
