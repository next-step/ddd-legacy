package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import java.util.NoSuchElementException;

import static kitchenpos.application.MenuFixture.createMenuRequest;
import static kitchenpos.application.MenuGroupFixture.createMenuGroupRequest;
import static org.assertj.core.api.Assertions.*;

class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();


    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴 가격이 0보다 작으면 예외가 발생한다.")
    @ValueSource(longs = {-1000L})
    @ParameterizedTest
    void create(final long price) {
        //given
        final Menu request = createMenuRequest(price);
        // when, then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴의 메뉴 그룹 ID가 메뉴 그룹 저장소에 없으면 예외가 발생한다.")
    @Test
    void create2() {
        //given
        final MenuGroup requestMenuGroup = createMenuGroupRequest("한마리메뉴");
        MenuGroup actualMenuGroup = menuGroupRepository.save(requestMenuGroup);

        final MenuGroup request2MenuGroup = createMenuGroupRequest("두마리메뉴");
        final Menu request = createMenuRequest(20_000L, request2MenuGroup.getId());

        // when, then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(request));

    }




}