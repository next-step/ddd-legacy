package kitchenpos.application;

import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
public class MenuGroupTest {

    private MenuGroupService menuGroupService;
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴그룹 생성")
    @Test
    void create() {
        MenuGroup actual = menuGroupRequest("치킨류");
        MenuGroup expected = menuGroupService.create(actual);

        assertAll(
                () -> assertThat(expected.getId()).isNotNull(),
                () -> assertThat(expected.getName()).isEqualTo(actual.getName())
        );
    }

    @DisplayName("메뉴그룹 생성 - null 또는 \"\" 을 생성될 메뉴그룹의 이름으로 지정할 수 없다")
    @EmptySource
    @NullSource
    @ParameterizedTest
    void createValidationName(String name) {
        MenuGroup actual = menuGroupRequest(name);
        assertThatThrownBy(() -> menuGroupService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("모든 메뉴그룹을 조회할 수 있다")
    @Test
    void findAll() {
        menuGroupRepository.save(new MenuGroup("testName1"));
        menuGroupRepository.save(new MenuGroup("testName2"));
        assertThat(menuGroupService.findAll().size())
                .isEqualTo(2);
    }

    private MenuGroup menuGroupRequest(String name) {
        return new MenuGroup(name);
    }

}