package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    private MenuGroupService sut;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        sut = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 신규 등록한다")
    @Test
    void testCreate() {
        // given
        String menuName = "test menu name";
        var expected = MenuGroupFixture.create(menuName);

        given(menuGroupRepository.save(Mockito.any(MenuGroup.class))).willReturn(expected);

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

    @DisplayName("모든 메뉴 그룹을 조회한다")
    @Test
    void testFindAll() {
        // given
        var menuGroups = List.of(
            MenuGroupFixture.create("menuGroup1"),
            MenuGroupFixture.create("menuGroup2")
        );

        given(menuGroupRepository.findAll()).willReturn(menuGroups);

        // when
        List<MenuGroup> actual = sut.findAll();

        // then
        assertThat(actual.size()).isEqualTo(menuGroups.size());
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(menuGroups.get(0));
        assertThat(actual.get(1)).usingRecursiveComparison().isEqualTo(menuGroups.get(1));
    }
}
