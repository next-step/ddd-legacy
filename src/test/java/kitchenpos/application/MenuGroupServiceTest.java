package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    MenuGroupRepository menuGroupRepository;

    @InjectMocks
    MenuGroupService sut;

    @DisplayName("메뉴그룹은 이름을 가진다")
    @Test
    public void create() {
        MenuGroup request = new MenuGroup();
        request.setName("치킨류");

        sut.create(request);

        Mockito.verify(menuGroupRepository).save(Mockito.any());
    }

    @ParameterizedTest
    @DisplayName("이름은 빈 값이거나 빈 문자열일 수 없다.")
    @NullAndEmptySource
    public void noEmptyOrNullName(String name) {
        MenuGroup request = new MenuGroup();
        request.setName(name);

        assertThatThrownBy(() -> {
            sut.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(menuGroupRepository);
    }
}