package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.MenuGroupAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
class MenuGroupAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("메뉴 그룹을 생성 하고 조회할 수 있다.")
    @Test
    void menuGroupCreateAndFindAllAcceptance() {
        // 메뉴 그룹 생성
        final Response recommendMenuGroupResponse = MenuGroupAcceptanceStep.create(createMenuGroup("추천메뉴"));
        final UUID recommendMenuGroupId = UUID.fromString(recommendMenuGroupResponse.asString());

        // 메뉴 그룹 전체 목록 조회
        final Response findAllResponse = MenuGroupAcceptanceStep.findAll();
        
        assertThat(findAllResponse.getBody().jsonPath().getList("id")).contains(recommendMenuGroupId);
    }
}
