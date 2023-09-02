package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.testfixture.FakeMenuGroupRepository;
import kitchenpos.testfixture.MenuGroupFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new FakeMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);

        menuGroupRepository.save(
                MenuGroupFixture.createMenuGroup("메뉴그룹테스트")
        );
    }

    @Test
    @DisplayName("메뉴를 등록하기 위해 메뉴그룹을 생성할 수 있다.")
    void create1() {
        var request = MenuGroupFixture.createMenuGroup("메뉴그룹");

        var result = menuGroupService.create(request);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result.getId()).isNotNull();
            softly.assertThat(result.getName()).isEqualTo(request.getName());
        });
    }

    @ParameterizedTest(name = "메뉴 그룹의 이름은 공백이거나 빈 값일 수 없다. (input={0})")
    @NullAndEmptySource
    void create2(String input) {
        var request = MenuGroupFixture.createMenuGroup(input);

        var throwable = catchThrowable(() -> menuGroupService.create(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 메뉴 그룹을 리스트로 조회할 수 있다.")
    void findAll() {
        var result = menuGroupService.findAll();

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result).isNotEmpty();
        });
    }
}
