package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuApi.*;
import static kitchenpos.acceptance.MenuGroupApi.메뉴그룹_생성;
import static kitchenpos.acceptance.MenuGroupData.추천_메뉴;
import static kitchenpos.acceptance.ProductApi.상품_생성;
import static kitchenpos.acceptance.ProductData.강정치킨;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MenuAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 메뉴_생성__성공() throws Exception {
        String menuGroupId = 메뉴그룹_생성(mockMvc, 추천_메뉴.getValue());
        String productId = 상품_생성(mockMvc, 강정치킨.getValue());

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
        String menuGroupId = 메뉴그룹_생성(mockMvc, 추천_메뉴.getValue());
        String productId = 상품_생성(mockMvc, 강정치킨.getValue());

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
        String menuGroupId = 메뉴그룹_생성(mockMvc, 추천_메뉴.getValue());
        String productId = 상품_생성(mockMvc, 강정치킨.getValue());

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
        String menuGroupId = 메뉴그룹_생성(mockMvc, 추천_메뉴.getValue());
        String productId = 상품_생성(mockMvc, 강정치킨.getValue());

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
        String menuGroupId = 메뉴그룹_생성(mockMvc, 추천_메뉴.getValue());
        String productId = 상품_생성(mockMvc, 강정치킨.getValue());

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
