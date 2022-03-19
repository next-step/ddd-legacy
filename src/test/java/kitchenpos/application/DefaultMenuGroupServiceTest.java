package kitchenpos.application;

import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuGroupFixture.추천메뉴;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("메뉴 그룹")
class DefaultMenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private DefaultMenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new DefaultMenuGroupService(menuGroupRepository);
    }

    @DisplayName("이름이 없으면 예외 발생")
    @ParameterizedTest(name = "이름: [{arguments}]")
    @NullAndEmptySource
    void createNonameException(String name) {
        //given
        MenuGroup 이름_없는_메뉴_그룹 = 메뉴_그룹_생성(name);

        //when
        ThrowingCallable actual = () -> menuGroupService.create(이름_없는_메뉴_그룹);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 생성")
    @Test
    void create() {
        //given
        MenuGroup 신규_메뉴_그룹 = 메뉴_그룹_생성(세트메뉴.getName());

        //when
        MenuGroup menuGroup = menuGroupService.create(신규_메뉴_그룹);

        //then
        assertAll(
            () -> assertThat(menuGroup.getId()).isNotNull(),
            () -> assertThat(menuGroup.getName()).isEqualTo(세트메뉴.getName())
        );
    }

    @DisplayName("모든 메뉴 그룹 조회")
    @Test
    void findAll() {
        //given
        menuGroupRepository.save(추천메뉴);
        menuGroupRepository.save(세트메뉴);

        //when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertAll(
            () -> assertThat(menuGroups).hasSize(2),
            () -> assertThat(menuGroups).extracting("name")
                .contains(세트메뉴.getName(), 추천메뉴.getName())
        );
    }

    private MenuGroup 메뉴_그룹_생성(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
