package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixtures.메뉴_그룹_등록;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("메뉴그룹")
public class MenuGroupServiceTest extends ApplicationTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹 전체 조회한다.")
    @Test
    public void findAll() {
        // given
        MenuGroup 등심_메뉴_그룹 = 메뉴_그룹_등록("등심세트");
        MenuGroup 안심_메뉴_그룹 = 메뉴_그룹_등록("안심세트");
        given(menuGroupRepository.findAll()).willReturn(Arrays.asList(등심_메뉴_그룹, 안심_메뉴_그룹));

        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertAll(
                () -> assertThat(menuGroups.size()).isEqualTo(2),
                () -> assertThat(menuGroups).containsExactly(등심_메뉴_그룹, 안심_메뉴_그룹)
        );
    }

    @DisplayName("메뉴 그룹을 등록한다.")
    @ParameterizedTest
    @ValueSource(strings = {"등심세트"})
    public void create(String name) {
        // given
        MenuGroup menuGroup = 메뉴_그룹_등록(name);
        given(menuGroupRepository.save(any())).willReturn(menuGroup);


        // when
        MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        // then
        assertAll(
                () -> assertThat(createdMenuGroup.getId()).isInstanceOf(UUID.class),
                () -> assertThat(createdMenuGroup.getName()).isEqualTo(name)
        );

    }

    @DisplayName("메뉴 그룹 등록시 이름을 체크 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithName(String name) {
        // given
        MenuGroup emptyMenu = 메뉴_그룹_등록(name);

        // when, then
        assertThatThrownBy(() -> menuGroupService.create(emptyMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }


}