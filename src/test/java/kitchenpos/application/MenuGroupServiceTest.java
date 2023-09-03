package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kitchenpos.Fixtures.메뉴그룹_생성;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("메뉴 그룹 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private static final String 한식 = "한식";

    private static final String 일식 = "일식";

    private static final String 공백 = "";

    @Test
    @DisplayName("메뉴그룹을 등록한다")
    void saveMenuGroup() {
        // given
        final MenuGroup 요청_객체 = 메뉴그룹_생성(한식);

        given(menuGroupRepository.save(any()))
                .willReturn(요청_객체);
        // when
        final MenuGroup 응답_결과 = menuGroupService.create(요청_객체);

        // then
        assertThat(응답_결과).isNotNull();
    }

    @Test
    @DisplayName("메뉴그룹 이름은 비어있거나 공백일 수 없다")
    void requireMenuGroupName() {
        // given
        final MenuGroup request = 메뉴그룹_생성(공백);

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹 목록을 조회한다")
    void findAllByMenuGroup() {
        // given
        final MenuGroup 요청_객체_1 = 메뉴그룹_생성(한식);
        final MenuGroup 요청_객체_2 = 메뉴그룹_생성(일식);

        given(menuGroupRepository.findAll())
                .willReturn(List.of(요청_객체_1, 요청_객체_2));

        // when
        final List<MenuGroup> 응답_결과 = menuGroupService.findAll();

        // then
        assertThat(응답_결과).hasSize(2);
    }

}