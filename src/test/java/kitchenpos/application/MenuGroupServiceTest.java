package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @InjectMocks
    MenuGroupService menuGroupService;
    @Mock
    MenuGroupRepository menuGroupRepository;

    @DisplayName(value = "메뉴그룹을 등록할 수 있다")
    @Test
    void create_success() throws Exception {
        //given

        //when

        //then

    }

    @DisplayName(value = "반드시 한글자 이상의 메뉴그룹명을 가진다")
    @Test
    void create_fail_invalid_name() throws Exception {
        //given

        //when

        //then

    }

    @DisplayName(value = "전체 메뉴그룹을 조회할 수 있다")
    @Test
    void findAll_success() throws Exception {
        //given

        //when

        //then

    }
}