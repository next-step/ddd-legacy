package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴를 등록하기 위해 메뉴그룹을 생성할 수 있다.")
    void create1() {
        var request = MenuGroupFixture.createMenuGroup("메뉴그룹");
        given(menuGroupRepository.save(any())).willReturn(request);

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
        var request = MenuGroupFixture.createMenuGroup("메뉴그룹");
        given(menuGroupRepository.findAll()).willReturn(List.of(request));

        var result = menuGroupService.findAll();

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result.get(0).getId()).isEqualTo(request.getId());
            softly.assertThat(result.get(0).getName()).isEqualTo(request.getName());
        });
    }
}
