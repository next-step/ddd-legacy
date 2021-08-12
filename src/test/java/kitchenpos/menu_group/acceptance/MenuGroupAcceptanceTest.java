package kitchenpos.menu_group.acceptance;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.menu_group.fixture.MenuGroupSaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static kitchenpos.menu_group.step.MenuGroupStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MenuGroup 인수 테스트")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

    private MenuGroupSaveRequest 한마리메뉴;
    private MenuGroupSaveRequest 두마리메뉴;

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        한마리메뉴 = createMenuGroupSaveRequest("한마리메뉴");
        두마리메뉴 = createMenuGroupSaveRequest("두마리메뉴");
        menuGroupRepository.deleteAll();
    }

    @DisplayName("메뉴 그룹을 등록한다")
    @Test
    public void create() {
        // when
        ExtractableResponse<Response> response = requestCreateMenuGroup(두마리메뉴);

        // then
        assertCreateMenuGroup(response);
    }

    @DisplayName("등록된 메뉴그룹을 모두 조회한다")
    @Test
    public void findAll() {
        // given
        completeCreateMenuGroup(한마리메뉴);
        completeCreateMenuGroup(두마리메뉴);

        // when
        ExtractableResponse<Response> response = requestFindAllMenuGroup();

        // then
        assertFindAllMenuGroup(response);
    }

    private void assertFindAllMenuGroup(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(new TypeRef<List<MenuGroup>>() {}).size()).isEqualTo(2);
    }

    private void assertCreateMenuGroup(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.as(MenuGroup.class).getId()).isNotNull();
        assertThat(response.as(MenuGroup.class).getName()).isEqualTo("두마리메뉴");
    }
}
