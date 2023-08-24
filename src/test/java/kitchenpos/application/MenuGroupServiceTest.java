package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MenuGroupService 클래스")
@SpringBootTest
class MenuGroupServiceTest {
    @Autowired
    private MenuGroupService sut;

    @Autowired
    private DatabaseCleanStep databaseCleanStep;

    @BeforeEach
    void setUp() {
        databaseCleanStep.clean();
    }

    @DisplayName("새로운 메뉴 그룹을 등록할 수 있다.")
    @Test
    void create() {
        // given
        MenuGroup menuGroup = MenuGroupTestFixture.create().changeId(null).getMenuGroup();

        // when
        MenuGroup result = sut.create(menuGroup);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 메뉴 그룹");
    }

    @DisplayName("메뉴 그룹의 이름이 null이거나 빈 문자열일 경우 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithInvalidName(String name) {
        // given
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(null)
                .changeName(name)
                .getMenuGroup();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.create(menuGroup));
    }
}