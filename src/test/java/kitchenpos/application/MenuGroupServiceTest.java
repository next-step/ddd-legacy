package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static kitchenpos.fixture.domain.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.request.MenuGroupRequestFixture.createRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;


class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void beforeEach() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    @DisplayName("카테고리를 생성한다")
    void menuGroupCrate() {
        // given
        MenuGroup actual = createRequest("일식");

        // when
        MenuGroup expect = menuGroupService.create(actual);

        // then
        assertAll(
                () -> assertThat(expect.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expect.getName())
        );
    }


    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("카테고리 생성 시 이름이 비어있으면 생성할 수 없다")
    void menuGroupCreateNotName(String input) {
        // given
        MenuGroup actual = createRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuGroupService.create(actual)
        );
    }

    @Test
    @DisplayName("카테고리를 조회한다")
    void findMenuGroup() {
        // given
        menuGroupRepository.save(menuGroup("일식"));

        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        assertThat(result).hasSize(1);
    }

}
