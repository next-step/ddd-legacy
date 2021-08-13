package kitchenpos.application;

import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP1;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP1_REQUEST;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP2;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP2_REQUEST_SAME_NAME;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_WITH_NAME_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MenuGroupServiceTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("create - 메뉴 그룹을 추가할 수 있다")
    @Test
    void createOK() {
        //given
        final MenuGroup menuGroupRequest = MENU_GROUP1_REQUEST();

        //when
        final MenuGroup sut = menuGroupService.create(menuGroupRequest);

        //then
        assertAll(
            () -> assertThat(sut.getId()).isNotNull(),
            () -> assertThat(sut.getName()).isEqualTo(menuGroupRequest.getName())
        );
    }

    @DisplayName("create - 메뉴 그룹 이름이 한글자 이상이 아니라면 예외를 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithEmptyName(final String value) {
        //given
        final MenuGroup menuGroupRequest = MENU_GROUP_WITH_NAME_REQUEST(value);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuGroupService.create(menuGroupRequest));
    }

    @DisplayName("create - 메뉴 그룹 이름은 중복될 수 있다")
    @Test
    void createWithDuplicateName() {
        //given
        final MenuGroup menuGroup1Request = MENU_GROUP1_REQUEST();
        final MenuGroup menuGroup2RequestSameName = MENU_GROUP2_REQUEST_SAME_NAME();

        //when
        final MenuGroup sut1 = menuGroupService.create(menuGroup1Request);
        final MenuGroup sut2 = menuGroupService.create(menuGroup2RequestSameName);

        //then
        assertAll(
            () -> assertThat(sut1.getId()).isNotEqualTo(sut2.getId()),
            () -> assertThat(sut1.getName()).isEqualTo(sut2.getName())
        );
    }

    @DisplayName("findAll - 메뉴 그룹 리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        final MenuGroup menuGroup1 = MENU_GROUP1();
        final MenuGroup menuGroup2 = MENU_GROUP2();

        menuGroupRepository.save(menuGroup1);
        menuGroupRepository.save(menuGroup2);

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertAll(
            () -> assertThat(menuGroups).hasSize(TWO),
            () -> assertThat(menuGroups.get(ZERO)
                .getId()).isNotEqualTo(menuGroups.get(ONE)
                .getId()),
            () -> assertThat(menuGroup1.getName()).isEqualTo(menuGroups.get(ZERO)
                .getName()),
            () -> assertThat(menuGroup2.getName()).isEqualTo(menuGroups.get(ONE)
                .getName())
        );
    }

}
