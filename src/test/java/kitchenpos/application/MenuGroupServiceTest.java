package kitchenpos.application;

import kitchenpos.domain.FakeMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static kitchenpos.exception.MenuGroupExceptionMessage.NULL_EMPTY_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MenuGroupServiceTest {

    private final MenuGroupRepository repository = new FakeMenuGroupRepository();
    private final MenuGroupService service = new MenuGroupService(repository);

    @DisplayName("메뉴 그룹 생성 성공")
    @Test
    void create_success() {
        MenuGroup request = new MenuGroup();
        request.setName("치킨메뉴");

        MenuGroup result = service.create(request);

        assertThat(result.getName()).isEqualTo(request.getName());
    }

    @DisplayName("메뉴그룹 이름이 null, 빈문자열 이면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_null_empty(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        assertThatThrownBy(() -> service.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NULL_EMPTY_NAME);
    }

}
