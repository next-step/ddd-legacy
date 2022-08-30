package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void beforeEach() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    @DisplayName("카테고리를 생성한다")
    void menuGroupCrate() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("일식");
        given(menuGroupRepository.save(any())).willReturn(menuGroup);

        // when
        MenuGroup result = menuGroupService.create(menuGroup);

        // then
        assertThat(result).isEqualTo(menuGroup);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("카테고리 생성 시 이름이 비어있으면 생성할 수 없다")
    void menuGroupCreateNotName(String input) {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuGroupService.create(menuGroup)
        );
    }

    @Test
    @DisplayName("카테고리를 조회한다")
    void findMenuGroup() {
        // given
        MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setName("일식");
        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setName("중식");
        given(menuGroupRepository.findAll()).willReturn(
                List.of(menuGroup1, menuGroup2)
        );

        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        assertThat(result).containsExactly(menuGroup1, menuGroup2);
    }

}
