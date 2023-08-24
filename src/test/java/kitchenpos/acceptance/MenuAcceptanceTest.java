package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuApi.*;
import static kitchenpos.acceptance.MenuGroupApi.*;
import static kitchenpos.acceptance.MenuGroupData.*;
import static kitchenpos.acceptance.ProductApi.*;
import static kitchenpos.acceptance.ProductData.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback
public class MenuAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 메뉴_생성__성공() throws Exception {
        MockHttpServletResponse menuGroupCreatedResponse = 메뉴그룹_생성_요청(mockMvc, 추천_메뉴.getValue());
        MockHttpServletResponse productCreatedResponse = 상품_생성_요청(mockMvc, 강정치킨.getValue());
        String menuGroupId = extractMenuGroupId(menuGroupCreatedResponse);
        String productId = extractProductId(productCreatedResponse);

        Map<String, Object> request = Map.of(
                "name", "후라이드+후라이드",
                "price", 19000,
                "menuGroupId", menuGroupId,
                "displayed", true,
                "menuProducts", List.of(
                        Map.of("productId", productId,
                                "quantity", 2)
                ));

        MockHttpServletResponse response = 메뉴_생성_요청(mockMvc, request);

        메뉴_생성_성공함(response);
    }

    @Test
    void 메뉴_가격_변경__성공() throws Exception {
        MockHttpServletResponse menuGroupCreatedResponse = 메뉴그룹_생성_요청(mockMvc, 추천_메뉴.getValue());
        MockHttpServletResponse productCreatedResponse = 상품_생성_요청(mockMvc, 강정치킨.getValue());
        String menuGroupId = extractMenuGroupId(menuGroupCreatedResponse);
        String productId = extractProductId(productCreatedResponse);

        Map<String, Object> createRequest = Map.of(
                "name", "후라이드+후라이드",
                "price", 19000,
                "menuGroupId", menuGroupId,
                "displayed", true,
                "menuProducts", List.of(
                        Map.of("productId", productId,
                                "quantity", 2)
                ));
        MockHttpServletResponse createdResponse = 메뉴_생성_요청(mockMvc, createRequest);

        String menuId = extractMenuId(createdResponse);
        Map<String, Object> request = Map.of("price", 17000);

        MockHttpServletResponse response = 메뉴_가격_변경_요청(mockMvc, menuId, request);

        메뉴_가격_변경_성공함(response);
    }

    @Test
    void 메뉴_보이기_설정__성공() throws Exception {
        MockHttpServletResponse menuGroupCreatedResponse = 메뉴그룹_생성_요청(mockMvc, 추천_메뉴.getValue());
        MockHttpServletResponse productCreatedResponse = 상품_생성_요청(mockMvc, 강정치킨.getValue());
        String menuGroupId = extractMenuGroupId(menuGroupCreatedResponse);
        String productId = extractProductId(productCreatedResponse);

        Map<String, Object> createRequest = Map.of(
                "name", "후라이드+후라이드",
                "price", 19000,
                "menuGroupId", menuGroupId,
                "displayed", true,
                "menuProducts", List.of(
                        Map.of("productId", productId,
                                "quantity", 2)
                ));

        MockHttpServletResponse createdResponse = 메뉴_생성_요청(mockMvc, createRequest);

        String menuId = extractMenuId(createdResponse);
        MockHttpServletResponse response = 메뉴_보이기_설정_요청(mockMvc, menuId);

        메뉴_보이기_설정_성공함(response);
    }

    @Test
    void 메뉴_숨기기_설정__성공() throws Exception {
        MockHttpServletResponse menuGroupCreatedResponse = 메뉴그룹_생성_요청(mockMvc, 추천_메뉴.getValue());
        MockHttpServletResponse productCreatedResponse = 상품_생성_요청(mockMvc, 강정치킨.getValue());
        String menuGroupId = extractMenuGroupId(menuGroupCreatedResponse);
        String productId = extractProductId(productCreatedResponse);

        Map<String, Object> createRequest = Map.of(
                "name", "후라이드+후라이드",
                "price", 19000,
                "menuGroupId", menuGroupId,
                "displayed", true,
                "menuProducts", List.of(
                        Map.of("productId", productId,
                                "quantity", 2)
                ));

        MockHttpServletResponse createdResponse = 메뉴_생성_요청(mockMvc, createRequest);

        String menuId = extractMenuId(createdResponse);
        MockHttpServletResponse response = 메뉴_숨기기_설정_요청(mockMvc, menuId);

        메뉴_숨기기_설정_성공함(response);
    }

    @Test
    void 메뉴_전체조회() throws Exception {
        MockHttpServletResponse menuGroupCreatedResponse = 메뉴그룹_생성_요청(mockMvc, 추천_메뉴.getValue());
        MockHttpServletResponse productCreatedResponse = 상품_생성_요청(mockMvc, 강정치킨.getValue());
        String menuGroupId = extractMenuGroupId(menuGroupCreatedResponse);
        String productId = extractProductId(productCreatedResponse);

        Map<String, Object> createRequest = Map.of(
                "name", "후라이드+후라이드",
                "price", 19000,
                "menuGroupId", menuGroupId,
                "displayed", true,
                "menuProducts", List.of(
                        Map.of("productId", productId,
                                "quantity", 2)
                ));
        메뉴_생성_요청(mockMvc, createRequest);

        MockHttpServletResponse response = 메뉴_전체조회_요청(mockMvc);

        메뉴_전체조회_성공함(response, 1);
    }

}
