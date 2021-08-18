package kitchenpos.application;

import kitchenpos.builder.MenuGroupBuilder;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.mock.MockMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuGroupServiceTest {
    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new MockMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("이름으로 메뉴 그룹을 추가한다")
    @Test
    void create() {
        final MenuGroup expected = MenuGroupBuilder.newInstance()
                .build();

        final MenuGroup actual = menuGroupService.create(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(menuGroupRepository.findById(actual.getId())
                        .isPresent()
                ).isTrue()
        );
    }

    @DisplayName("이름은 필수고, 빈 문자열이 아니어야 한다")
    @ParameterizedTest
    @NullAndEmptySource
    void create(final String name) {
        final MenuGroup expected = MenuGroupBuilder.newInstance()
                .setName(name)
                .build();

        assertThatThrownBy(() -> menuGroupService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 전체 목록을 조회한다")
    @Test
    void findAll() {
        final int expected = 2;

        IntStream.range(0, expected)
                .mapToObj(index -> MenuGroupBuilder.newInstance().build())
                .forEach(menuGroupRepository::save);

        assertThat(menuGroupService.findAll()).hasSize(expected);
    }
}
