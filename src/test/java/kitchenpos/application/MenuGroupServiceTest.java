package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Stream;
import kitchenpos.domain.MenuGroup;
import kitchenpos.testHelper.SpringBootTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @DisplayName("메뉴그룹을 등록할수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"메뉴그룹1", "메뉴그룹2", "메뉴그룹3", "메뉴 그룹 4", "메뉴%그룹@5"})
    void test2(String name) {
        //given
        MenuGroup request = new MenuGroup(name);

        //when
        MenuGroup result = menuGroupService.create(request);

        //then
        assertAll(
            () -> assertThat(result.getId()).isNotNull(),
            () -> assertThat(result.getName()).isEqualTo(request.getName())
        );
    }

    @DisplayName("모든 메뉴그룹의 정보를 조회할수 있다.")
    @ParameterizedTest
    @MethodSource("test3MethodSource")
    void test3(List<String> names) {
        //given
        for (String name : names) {
            MenuGroup request = new MenuGroup(name);
            menuGroupService.create(request);
        }

        //when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertAll(
            () -> assertThat(menuGroups).extracting("name")
                .containsExactlyElementsOf(names),
            () -> assertThat(menuGroups).extracting("id")
                .doesNotContainNull()
        );

    }

    static Stream<List<String>> test3MethodSource() {
        return Stream.of(
            List.of("메뉴그룹1", "MenuGroup2", "메#뉴$그%룹^3"),
            List.of(" 메 뉴 그 룹 1 ", " 메 뉴 그 룹 2", "메 뉴 그 룹 3")
        );
    }
}