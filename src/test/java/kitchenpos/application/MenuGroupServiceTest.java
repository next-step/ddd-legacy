package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.repository.MenuGroupFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MenuGroupServiceTest {

    private MenuGroupService sut;

    private MenuGroupFakeRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new MenuGroupFakeRepository();
        sut = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    class 메뉴_그룹_등록 {

        @DisplayName("메뉴 그룹을 신규 등록한다")
        @Test
        void testCreate() {
            // given
            String menuName = "test menu name";
            var expected = MenuGroupFixture.create(menuName);

            // when
            MenuGroup actual = sut.create(expected);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getName()).isEqualTo(expected.getName());
        }

        @DisplayName("메뉴 그룹 이름은 없거나 공백으로 등록할 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void testCreateWhenNameIsInvalidThenThrowException(String menuName) {
            // given
            var expected = MenuGroupFixture.create(menuName);

            // when // then
            assertThatThrownBy(() -> sut.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 메뉴_그룹_조회 {

        @DisplayName("모든 메뉴 그룹을 조회한다")
        @Test
        void testFindAll() {
            // given
            MenuGroup menuGroup1 = MenuGroupFixture.create("menuGroup1");
            menuGroupRepository.save(menuGroup1);

            MenuGroup menuGroup2 = MenuGroupFixture.create("menuGroup2");
            menuGroupRepository.save(menuGroup2);

            // when
            List<MenuGroup> actual = sut.findAll();

            // then
            assertThat(actual.size()).isEqualTo(2);
            assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(menuGroup1);
            assertThat(actual.get(1)).usingRecursiveComparison().isEqualTo(menuGroup2);
        }
    }
}
