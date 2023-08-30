package kitchenpos.ui;

import kitchenpos.domain.MenuGroup;
import kitchenpos.util.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroup;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroupWithName;
import static org.hamcrest.Matchers.equalTo;


class MenuGroupAcceptanceTest extends AcceptanceTest {


    @DisplayName("메뉴 그룹을 생성한다")
    @Test
    void createMenuGroup() throws Exception {
        // given
        final MenuGroup menuGroup = generateMenuGroup();

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(menuGroup))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .assertThat()
                .body("name", equalTo(menuGroup.getName()))
        ;
    }

    @DisplayName("메뉴 그룹의 이름을 반드시 지정해야 한다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmptyName(final String name) throws Exception {
        // given
        final MenuGroup menuGroup = generateMenuGroupWithName(name);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(menuGroup))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("메뉴 그룹 목록을 조회한다")
    @Test
    void getAllMenuGroups() {
        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @Override
    protected String getPath() {
        return "/api/menu-groups";
    }
}