package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kitchenpos.domain.MenuGroup;
import kitchenpos.testHelper.SpringBootTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

class MenuGroupServiceTest extends SpringBootTestHelper {

    @Autowired
    MenuGroupService menuGroupService;

    @BeforeEach
    public void init() {
        super.init();
    }

    @DisplayName("메뉴그룹의 이름은 반드시 있어야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String name) {
        //given
        MenuGroup request = new MenuGroup(name);

        //when && then
        assertThatThrownBy(
            () -> menuGroupService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

}