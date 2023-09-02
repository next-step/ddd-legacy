package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    MenuGroupRepository menuGroupRepository;
    MenuGroupService menuGroupService;

    @BeforeEach
    void setup() {
        this.menuGroupService = new MenuGroupService(menuGroupRepository);
    }


    @DisplayName("그룹명이 입력되지 않으면 예외를 반환한다")
    @Test
    void name() {
        MenuGroup menuGroup = new MenuGroup();
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
