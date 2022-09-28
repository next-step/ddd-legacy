package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import kitchenpos.domain.FakeMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();

    // SUT

    private final MenuGroupService menuGroupService = new MenuGroupService(
            this.menuGroupRepository
    );

    @DisplayName("유효한 이름으로 메뉴 그룹을 생성할 수 있다.")
    @ValueSource(strings = {
            "study", "castle", "organize", "madden", "wall",
            "it", "next", "direct", "ticket", "piece",
    })
    @ParameterizedTest
    void djyjmigp(final String name) {
        // given
        final MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setName(name);

        // when
        final MenuGroup menuGroup = this.menuGroupService.create(requestMenuGroup);

        // then
        assertThat(menuGroup.getName()).isEqualTo(name);
    }

    @DisplayName("이름은 null이거나 빈 문자열일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void tzukyuah(final String name) {
        // given
        final MenuGroup requestMenuGroup = new MenuGroup();
        requestMenuGroup.setName(name);

        // when / then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> this.menuGroupService.create(requestMenuGroup));
    }

    @DisplayName("메뉴 그룹을 모두 조회할 수 있다.")
    @ValueSource(ints = {
            6, 21, 31, 9, 5,
            2, 7, 17, 18, 28,
    })
    @ParameterizedTest
    void cpgksbnc(final int size) {
        // given
        IntStream.range(0, size)
                .forEach(n -> {
                    final MenuGroup menuGroup = new MenuGroup();
                    menuGroup.setId(UUID.randomUUID());
                    menuGroup.setName(String.valueOf(n));
                    this.menuGroupRepository.save(menuGroup);
                });

        // when
        final List<MenuGroup> menuGroups = this.menuGroupService.findAll();

        // then
        assertThat(menuGroups).hasSize(size);
    }

    @DisplayName("메뉴 그룹을 생성한 후 모두 조회할 수 있다.")
    @ValueSource(ints = {
            6, 21, 31, 9, 5,
            2, 7, 17, 18, 28,
    })
    @ParameterizedTest
    void gakhgird(final int size) {
        // given
        IntStream.range(0, size)
                .forEach(n -> {
                    final MenuGroup menuGroup = new MenuGroup();
                    menuGroup.setName(String.valueOf(n));
                    this.menuGroupService.create(menuGroup);
                });

        // when
        final List<MenuGroup> menuGroups = this.menuGroupService.findAll();

        // then
        assertThat(menuGroups).hasSize(size);
    }

    @DisplayName("메뉴 그룹이 없는 상태에서 모두 조회시 빈 list가 반환되어야 한다.")
    @Test
    void kynbfwbl() {
        // when
        final List<MenuGroup> menuGroups = this.menuGroupService.findAll();

        // then
        assertThat(menuGroups).isEmpty();
    }
}
