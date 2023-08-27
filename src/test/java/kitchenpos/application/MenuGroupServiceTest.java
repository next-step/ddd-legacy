package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.helper.MenuGroupHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuGroupServiceTest extends ApplicationTest {

    @Autowired
    private MenuGroupService menuGroupService;


    @DisplayName("새로운 메뉴 그룹을 등록한다.")
    @Nested
    class CreateMenuGroup {
        @DisplayName("메뉴 그룹명은 비어있을 수 없고, 255자를 초과할 수 없다.")
        @Nested
        class Policy1 {
            @DisplayName("메뉴 그룹명이 0자 초가 255자 이하인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(strings = {" ", "한", "a", "1", "메뉴 그룹명", "menu group name", "메뉴 그룹 A", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one."})
            void success1(final String name) {
                // Given
                MenuGroup menuGroup = MenuGroupHelper.create(name);

                // When
                MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

                // Then
                assertThat(createdMenuGroup.getName()).isEqualTo(name);
            }

            @DisplayName("메뉴 그룹명이 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(final String name) {
                // When
                MenuGroup menuGroup = MenuGroupHelper.create(name);

                // Then
                assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("메뉴 그룹명이 0자인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(strings = {""})
            void fail2(final String name) {
                // When
                MenuGroup menuGroup = MenuGroupHelper.create(name);

                // Then
                assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("메뉴 그룹명이 255자를 초과한 경우 (실패)")
            @ParameterizedTest
            @ValueSource(strings = {"Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one ."})
            void fail3(final String name) {
                // When
                MenuGroup menuGroup = MenuGroupHelper.create(name);

                // Then
                assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                        .isInstanceOf(DataIntegrityViolationException.class);
            }
        }
    }

    @DisplayName("모든 메뉴 그룹을 가져온다.")
    @Nested
    class FindAllMenuGroups {

        private List<MenuGroup> beforeCreatedMenuGroups;

        @BeforeEach
        void beforeEach() {
            beforeCreatedMenuGroups = IntStream.range(0, 50)
                    .mapToObj(n -> menuGroupService.create(MenuGroupHelper.create()))
                    .collect(toUnmodifiableList());
        }

        @DisplayName("모든 메뉴 그룹을 가져온다 (성공)")
        @Test
        void success1() {
            // When
            List<MenuGroup> allMenuGroups = menuGroupService.findAll();

            // Then
            assertThat(allMenuGroups.size()).isEqualTo(beforeCreatedMenuGroups.size());
        }
    }

}