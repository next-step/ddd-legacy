package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.helper.InMemoryMenuGroupRepository;
import kitchenpos.helper.MenuGroupFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MenuGroupServiceTest {

    private final InMemoryMenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService testTarget;

    @BeforeEach
    void setUp() {
        testTarget = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹 생성 테스트")
    @Nested
    class CreateTest {

        @DisplayName("메뉴 그룹을 생성 할 수 있다.")
        @Test
        void test01() {
            // given
            MenuGroup request = MenuGroupFixture.request("치킨");

            // when
            MenuGroup actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(request.getName())
            );
        }

        @DisplayName("메뉴 그룹 이름은 비어 있을 수 없다.")
        @ParameterizedTest(name = "[{index}] name={0}")
        @NullAndEmptySource
        void test02(String name) {
            // given
            MenuGroup request = MenuGroupFixture.request(name);

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }
    }

    @DisplayName("메뉴 그룹 목록 조회 테스트")
    @Nested
    class FindAllTest {

        @DisplayName("메뉴 그룹 목록을 조회 할 수 있다.")
        @Test
        void test01() {
            // given
            MenuGroup menuGroup1 = menuGroupRepository.save(MenuGroupFixture.CHICKEN);
            MenuGroup menuGroup2 = menuGroupRepository.save(MenuGroupFixture.PIZZA);

            // when
            List<MenuGroup> actual = testTarget.findAll();

            // then
            assertThat(actual)
                .anyMatch(menuGroup -> menuGroup.getId().equals(menuGroup1.getId()))
                .anyMatch(menuGroup -> menuGroup.getId().equals(menuGroup2.getId()))
                .hasSize(2);
        }
    }
}