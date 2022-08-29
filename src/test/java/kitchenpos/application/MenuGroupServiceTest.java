package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MenuGroupServiceTest extends InitTest {

    @Resource
    private MenuGroupService target;

    @Test
    @DisplayName("메뉴그룹은 이름을 가진다")
    public void create() {
        MenuGroup request = buildValidMenuGroup();

        target.create(request);
    }

    @ParameterizedTest
    @DisplayName("이름은 빈 값이거나 빈 문자열일 수 없다.")
    @NullAndEmptySource
    public void noEmptyOrNullName(String name) {
        MenuGroup request = buildValidMenuGroup();
        request.setName(name);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }
}