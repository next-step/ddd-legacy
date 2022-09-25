package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FakeMenuGroupRepositoryTest {

    // SUT

    private final FakeMenuGroupRepository fakeMenuGroupRepository = new FakeMenuGroupRepository();

    @DisplayName("메뉴 그룹이 저장되어야 한다.")
    @ValueSource(strings = {
            "best", "silver", "university", "FALSE", "caution",
            "appearance", "ready", "card", "official", "bath",
    })
    @ParameterizedTest
    void fhiwvslb(String name) {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);

        // when
        final MenuGroup savedMenuGroup = this.fakeMenuGroupRepository.save(menuGroup);

        // then
        assertThat(savedMenuGroup).isEqualTo(menuGroup);
    }

    @DisplayName("저장된 메뉴 그룹은 ID로 찾을 수 있어야 한다.")
    @Test
    void jxewmhhv() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("reflection");
        final MenuGroup savedMenuGroup = this.fakeMenuGroupRepository.save(menuGroup);

        // when
        final MenuGroup foundMenuGroup = this.fakeMenuGroupRepository.findById(
                        savedMenuGroup.getId())
                .orElse(null);

        // then
        assertThat(foundMenuGroup).isEqualTo(menuGroup);
    }

    @DisplayName("저장되지 않은 메뉴 그룹은 ID로 찾을 수 없어야 한다.")
    @Test
    void xwssrsnn() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("moderate");
        this.fakeMenuGroupRepository.save(menuGroup);

        // when
        final MenuGroup foundMenuGroup = this.fakeMenuGroupRepository.findById(UUID.randomUUID())
                .orElse(null);

        // then
        assertThat(foundMenuGroup).isNull();
    }

    @DisplayName("빈 상태에서 모두 조회시 빈 List가 반환되어야 한다.")
    @Test
    void uqdczcjk() {
        // when
        final List<MenuGroup> menuGroups = this.fakeMenuGroupRepository.findAll();

        // then
        assertThat(menuGroups).isEmpty();
    }

    @DisplayName("모두 조회시 저장된 수 만큼 조회되어야 한다.")
    @ValueSource(ints = {
            5, 22, 26, 10, 27,
            31, 18, 21, 15, 30,
    })
    @ParameterizedTest
    void ijzolgpe(int size) {
        // given
        for (int i = 0; i < size; i++) {
            final MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());
            menuGroup.setName(String.valueOf(i));
            this.fakeMenuGroupRepository.save(menuGroup);
        }

        // when
        final List<MenuGroup> menuGroups = this.fakeMenuGroupRepository.findAll();

        // then
        assertThat(menuGroups).hasSize(size);
    }
}
