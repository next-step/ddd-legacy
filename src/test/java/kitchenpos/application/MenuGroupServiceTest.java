package kitchenpos.application;

import kitchenpos.application.fakeobject.FakeMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuGroupServiceTest {
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        this.menuGroupService = new MenuGroupService(new FakeMenuGroupRepository());
    }

    @DisplayName("이름이 없을 경우 예외 발생한다.")
    @NullAndEmptySource
    @ParameterizedTest
    public void create_name_already_exist(String name) {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(menuGroup));
    }

    @DisplayName("이름이 있을 경우 추가 성공.")
    @ValueSource(strings = {"test"})
    @ParameterizedTest
    public void create_name_exist(String name) {
        //given
        MenuGroup menuGroupCreateRequest = createMenuGroupByName(name);

        //when
        MenuGroup result = menuGroupService.create(menuGroupCreateRequest);

        //then
        assertThat(result)
                .isNotNull();
    }


    @DisplayName("DB에 데이터가 존재할 경우 조회 데이터가 1개 이상이다.")
    @Test
    public void findAll_data_exist() {
        //given
        MenuGroup menuGroupCreateRequest = createMenuGroupByName("menu");
        menuGroupService.create(menuGroupCreateRequest);

        // when
        List<MenuGroup> list = menuGroupService.findAll();

        //then
        assertThat(list)
                .isNotEmpty();
    }

    private MenuGroup createMenuGroupByName(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
