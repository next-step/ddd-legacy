package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.ui.utils.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static kitchenpos.objectmother.MenuGroupMaker.*;
import static kitchenpos.ui.requestor.MenuGroupRequestor.메뉴그룹생성요청;
import static kitchenpos.ui.requestor.MenuGroupRequestor.메뉴그룹전체조회요청;
import static org.assertj.core.api.Assertions.assertThat;

class MenuGroupRestControllerTest extends ControllerTest {


    @DisplayName("메뉴그룹 생성요청 후 메뉴그룹시 메뉴그룹이 존재해야한다.")
    @Test
    void 메뉴그룹생성() {
        // when
        ExtractableResponse<Response> response = 메뉴그룹생성요청(메뉴그룹_1);

        // then
        메뉴그룹생성됨(response);
    }

    @DisplayName("메뉴그룹 생성요청시 이름이 존재하지 않으면 에러를 던진다.")
    @Test
    void 메뉴그룹생성_실패_이름미존재() {
        // when
        ExtractableResponse<Response> response = 메뉴그룹생성요청(메뉴그룹_이름없음);

        메뉴그룹생성실패됨(response);
    }

    @DisplayName("메뉴그룹 전체조회시 지금까지 등록된 메뉴그룹이 전부 조회되야한다.")
    @Test
    void 메뉴그룹전체조회() {
        // given
        메뉴그룹생성요청(메뉴그룹_1);
        메뉴그룹생성요청(메뉴그룹_2);

        // when
        ExtractableResponse<Response> response = 메뉴그룹전체조회요청();

        // then
        메뉴그룹전체조회됨(response);
    }

    private void 메뉴그룹생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 메뉴그룹생성실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 메뉴그룹전체조회됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("name", String.class))
                .hasSize(2)
                .containsExactly(메뉴그룹_1.getName(), 메뉴그룹_2.getName());
    }

}