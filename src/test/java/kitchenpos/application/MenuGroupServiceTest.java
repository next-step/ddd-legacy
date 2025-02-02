package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@UnitTest
@DisplayName("메뉴 그룹 서비스 테스트")
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService sut;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("메뉴 그룹 생성 - 성공: 유효한 이름")
    void createMenuGroup() {
        // given
        MenuGroup expected = MenuGroupFixture.추천_메뉴그룹();
        String expectedName = expected.getName();

        MenuGroup request = MenuGroupFixture.요청_메뉴그룹(expectedName);

        given(menuGroupRepository.save(any(MenuGroup.class))).willReturn(expected);

        // when
        MenuGroup saved = sut.create(request);

        // then
        assertThat(saved)
                .isNotNull()
                .extracting(MenuGroup::getName)
                .isEqualTo(expectedName);
        verify(menuGroupRepository).save(any(MenuGroup.class));
    }

    @Test
    @DisplayName("메뉴 그룹 생성 - 실패: 이름 미지정 이면 IllegalArgumentException 발생")
    void createMenuGroupWithoutNameThrowsIllegalArgumentException() {
        // given
        MenuGroup request = MenuGroupFixture.요청_메뉴그룹(null);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sut.create(request));
    }

    @Test
    @DisplayName("모든 메뉴 그룹을 조회할 수 있다.")
    void findAllMenuGroups() {
        // given
        List<MenuGroup> menuGroups = List.of(
                MenuGroupFixture.추천_메뉴그룹(),
                MenuGroupFixture.신_메뉴그룹()
        );
        given(menuGroupRepository.findAll()).willReturn(menuGroups);

        // when
        List<MenuGroup> result = sut.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(menuGroupRepository).findAll();
    }
}