package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹 등록하기")
    @Nested
    class MenuGroupCreateTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴그룹을 정삭적으로 등록한다")
        @Test
        void create_menu_group_successfully() {
            // given
            MenuGroup request = new MenuGroup();
            request.setName("한식");

            // when
            MenuGroup createdMenuGroup = menuGroupService.create(request);

            // then
            assertAll(
                    () -> assertThat(createdMenuGroup.getId()).isNotNull(),
                    () -> assertThat(createdMenuGroup.getName()).isEqualTo("한식")
            );
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("이름은 반드시 입력해야 합니다")
        @Test
        void name_must_be_input() {
            // given
            MenuGroup request = new MenuGroup();
            request.setName(null);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuGroupService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("메뉴그룹 조회하기")
    @Nested
    class MenuGroupFindAllTest {

        @SqlGroup({
                @Sql(value = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @DisplayName("모든 메뉴그룹을 조회할 수 있다")
        @Test
        void find_all_menu_groups() {
            // when
            List<MenuGroup> menuGroups = menuGroupService.findAll();

            // then
            assertThat(menuGroups).hasSize(4);
        }
    }
}
