package kitchenpos.application;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.test.UnitTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MenuGroupServiceTest extends UnitTestCase {

    @InjectMocks
    private MenuGroupService service;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("순살파닭두마리메뉴");
    }

    @DisplayName("메뉴 그룹 등록")
    @Nested
    class Create {

        @DisplayName("메뉴 그룹은 이름으로 등록한다.")
        @Test
        void success() {
            // when then
            assertThatCode(() -> service.create(menuGroup))
                    .doesNotThrowAnyException();
        }

        @DisplayName("그룹 이름은 비어 있을 수 없다.")
        @ParameterizedTest
        @NullAndEmptySource
        void error(String actual) {
            // given
            menuGroup.setName(actual);

            // when then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.create(menuGroup));
        }
    }


    @DisplayName("등록된 메뉴 그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        given(menuGroupRepository.findAll())
                .willReturn(List.of(menuGroup));

        // when then
        assertThat(service.findAll())
                .contains(menuGroup);
    }
}