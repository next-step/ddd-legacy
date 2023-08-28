package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuGroupServiceTest {

    private MenuGroupRepository repository;

    private MenuGroupService service;

    @BeforeEach
    void setUp() {
        repository = new MenuGroupFakeRepository();
        service = new MenuGroupService(repository);
    }

    @DisplayName("menuGroup을 생성 후 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = "dummy")
    void create_success(final String value) {

        // given
        final MenuGroup request = create(value);

        // when
        final MenuGroup actual = service.create(request);

        // then
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(request);
    }


    @DisplayName("menuGroup의 이름이 null이거나 비어있으면 예외를 발생시킨다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_request(final String value) {

        // given
        final MenuGroup request = create(value);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }


    private MenuGroup create(final String name) {
        final MenuGroup group = new MenuGroup();
        group.setName(name);

        return group;
    }

    @DisplayName("menuGroup들을 조회하여 반환한다.")
    @Test
    void findAll() {

        // given
        final MenuGroup dummy1 = repository.save(create("dummy1"));
        final MenuGroup dummy2 = repository.save(create("dummy2"));

        // when
        final List<MenuGroup> actual = service.findAll();

        // then
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .contains(dummy1, dummy2);
    }
}