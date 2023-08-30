package kitchenpos.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;

public class MenuGroupServiceTest {
    private final MenuGroupService menuGroupService = new MenuGroupService(null);

    @ParameterizedTest
    @NullAndEmptySource
    void 메뉴그룹_생성__실패_이름이_null_and_empty(String nullAndEmpty) {
        MenuGroup menuGroup = MenuGroupFixture.builder()
                .name(nullAndEmpty)
                .build();

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
