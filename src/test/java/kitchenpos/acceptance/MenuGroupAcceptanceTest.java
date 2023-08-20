package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.steps.MenuGroupSteps;
import kitchenpos.acceptance.steps.MenuSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴그룹")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

    private static final String NAME = "메뉴그룹";
    @DisplayName("메뉴그룹 등록")
    @Test
    void create(){
        //when
        ExtractableResponse<Response> response = MenuGroupSteps.메뉴그룹_생성(NAME);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("name"))
                        .isEqualTo(NAME)
        );
    }

}
