package kitchenpos.application.fake;

import kitchenpos.application.MenuGroupService;
import kitchenpos.application.fake.helper.InMemoryMenuGroupRepository;
import kitchenpos.application.fake.helper.MenuGroupFixtureFactory;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FakeMenuGroupServiceTest {


    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setup() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    private static Stream<String> provideMenuNameForNullAndEmptyString() {
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName("메뉴 그룹 등록 - 메뉴 그룹은 반드시 이름을 가져야 한다.")
    @MethodSource("provideMenuNameForNullAndEmptyString")
    @ParameterizedTest
    void create01(String name) {
        //given
        MenuGroup 등록할_메뉴_그룹 = new MenuGroupFixtureFactory.Builder()
                .name(name)
                .build();
        //when & then
        assertThatThrownBy(() -> menuGroupService.create(등록할_메뉴_그룹))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 등록 - 메뉴그룹을 등록 할 수 있다.")
    @Test
    void create02() {
        //given
        String 등록할_메뉴_그룹_이름 = "런치 세트 그룹";
        MenuGroup 메뉴_그룹_생성_요청 = new MenuGroupFixtureFactory.Builder()
                .name(등록할_메뉴_그룹_이름)
                .build();
        //when
        MenuGroup saved = menuGroupService.create(메뉴_그룹_생성_요청);
        //then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(등록할_메뉴_그룹_이름)
        );
    }

    @DisplayName("메뉴 그룹 조회 - 등록된 모든 메뉴 그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        menuGroupRepository.save(MenuGroupFixtureFactory.런체세트그룹);
        // when
        List<MenuGroup> groups = menuGroupService.findAll();
        //then
        assertAll(
                () -> assertThat(groups).hasSize(1),
                () -> assertThat(groups).contains(MenuGroupFixtureFactory.런체세트그룹)
        );
    }

}
