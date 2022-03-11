package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.acceptance.step.MenuGroupSteps.*;

@DisplayName("메뉴 그룹 관리 기능")
class MenuGroupAcceptanceTest extends AcceptanceTest {
    private static final String MENU_GROUP_NAME = "탕수육 세트";

    @DisplayName("메뉴 그룹을 관리한다")
    @Test
    void manageMenuGroup() {
        ExtractableResponse<Response> createResponse = 메뉴_그룹_등록_요청(MENU_GROUP_NAME);
        메뉴_그룹_생성_완료(createResponse);

        ExtractableResponse<Response> findResponse = 메뉴_그룹_목록_조회_요청();
        메뉴_그룹_목록_조회_완료(findResponse);
    }
}
