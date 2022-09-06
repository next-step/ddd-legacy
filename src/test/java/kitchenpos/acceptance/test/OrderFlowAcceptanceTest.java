package kitchenpos.acceptance.test;

import kitchenpos.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static kitchenpos.acceptance.test.OrderFlowAssertions.*;
import static kitchenpos.acceptance.test.OrderFlowSteps.*;

class OrderFlowAcceptanceTest extends AcceptanceTest {

    @DisplayName("배송 주문 시나리오")
    @Test
    void deliveryOrderFlow() {
        // 상품 3개를 생성한다.
        var 후라이드_치킨_response = 상품을_생성한다("후라이드 치킨", 10_000);
        생성_확인됨(후라이드_치킨_response);
        String 후라이드_치킨_id = 후라이드_치킨_response.jsonPath().getString("id");

        var 양념_치킨_response = 상품을_생성한다("양념 치킨", 12_000);
        생성_확인됨(양념_치킨_response);
        String 양념_치킨_id = 양념_치킨_response.jsonPath().getString("id");

        var 마늘_치킨_response = 상품을_생성한다("마늘 치킨", 12_000);
        생성_확인됨(마늘_치킨_response);
        String 마늘_치킨_id = 양념_치킨_response.jsonPath().getString("id");

        var 모든_상품_response = 모든_상품을_조회한다();
        총_개수가_일치한다(모든_상품_response, 3);

        // 메뉴 그룹 1개를 생성한다.
        var 메뉴_그룹_response = 메뉴_그룹을_생성한다("두마리 치킨");
        생성_확인됨(메뉴_그룹_response);

        String 두마리_치킨_id = 메뉴_그룹_response.jsonPath().getString("id");
        var 모든_메뉴_그룹_response = 모든_메뉴_그룹을_조회한다();
        총_개수가_일치한다(모든_메뉴_그룹_response, 1);

        // 메뉴 2개를 생성한다.
        List<MenuProductRequest> 후라이드_양념_세트_상품_리스트 = new ArrayList<>();
        후라이드_양념_세트_상품_리스트.add(메뉴_상품을_생성한다(후라이드_치킨_id, 1));
        후라이드_양념_세트_상품_리스트.add(메뉴_상품을_생성한다(양념_치킨_id, 1));
        var 후라이드_양념_response = 메뉴를_생성한다("<후라이드 + 양념> 세트", 22_000, 두마리_치킨_id, true, 후라이드_양념_세트_상품_리스트);
        생성_확인됨(후라이드_양념_response);
        var 후라이드_양념_세트_메뉴_id = 후라이드_양념_response.jsonPath().getString("id");

        List<MenuProductRequest> 후라이드_마늘_세트_상품_리스트 = new ArrayList<>();
        후라이드_마늘_세트_상품_리스트.add(메뉴_상품을_생성한다(후라이드_치킨_id, 1));
        후라이드_마늘_세트_상품_리스트.add(메뉴_상품을_생성한다(마늘_치킨_id, 1));
        var 후라이드_마늘_response = 메뉴를_생성한다("<후라이드 + 마늘> 세트", 22_000, 두마리_치킨_id, true, 후라이드_마늘_세트_상품_리스트);
        생성_확인됨(후라이드_마늘_response);
        var 후라이드_마늘_세트_메뉴_id = 후라이드_마늘_response.jsonPath().getString("id");

        var 모든_메뉴_response = 모든_메뉴를_조회한다();
        총_개수가_일치한다(모든_메뉴_response, 2);

        // 상품 가격을 낮춰서 가격 정책에 따라 메뉴가 숨겨지는지 확인하고, 상품과 메뉴 가격을 조정한 뒤 메뉴를 진열 상태로 만든다.
        var 상품_가격_하향_response = 상품_가격을_변경한다(양념_치킨_id, 10_000);
        가격이_일치한다(상품_가격_하향_response, 10_000);

        var 진열_시도_response = 메뉴를_진열한다(후라이드_양념_세트_메뉴_id);
        요청에_실패함(진열_시도_response);

        var 상품_가격_상향_response = 상품_가격을_변경한다(양념_치킨_id, 12_000);
        가격이_일치한다(상품_가격_상향_response, 12_000);

        var 메뉴_가격_변경_response = 메뉴_가격을_변경한다(후라이드_양념_세트_메뉴_id, 21_000);
        가격이_일치한다(메뉴_가격_변경_response, 21_000);

        var 진열_재시도_response = 메뉴를_진열한다(후라이드_양념_세트_메뉴_id);
        요청에_성공함(진열_재시도_response);
    }


}
