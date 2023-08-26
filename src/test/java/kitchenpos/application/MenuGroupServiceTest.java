package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    MenuGroupRepository menuGroupRepository;
    MenuGroupService menuGroupService;
    @BeforeEach
    void setup(){
        this.menuGroupService = new MenuGroupService(menuGroupRepository);
    }


    @DisplayName("그룹명이 입력되어야한다.")
    @Test
    void name(){
        MenuGroup menuGroup = new MenuGroup();
        assertThatThrownBy(()->menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);

    }


}
