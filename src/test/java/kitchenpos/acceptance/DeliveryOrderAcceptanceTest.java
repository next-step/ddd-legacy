package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.test_fixture.OrderTestFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static kitchenpos.acceptance.acceptance_step.MenuGroupStep.메뉴_그룹_등록된_상태다;
import static kitchenpos.acceptance.acceptance_step.MenuStep.*;
import static kitchenpos.acceptance.acceptance_step.OrderStep.*;
import static kitchenpos.acceptance.acceptance_step.ProductStep.상품이_등록된_상태다;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("배달주문 인수 테스트")
public class DeliveryOrderAcceptanceTest extends AcceptanceTestBase {

    @Nested
    class 새로운_베딜주문_등록_인수테스트 {
        @Test
        void 새로운_배달_주문_등록에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);

            Order 등록할_배달주문_정보 = 등록할_배달주문_정보_생성한다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_등록한다(등록할_배달주문_정보);

            // then
            배달주문_등록에_성공했다(response, "서울시 강남구", 등록할_배달주문_정보.getOrderLineItems());
        }

        @Test
        void 주문_유형을_입력하지_않으면_배달주문_등록에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록할_주문_정보 = 주문_유형이_없는_주문_정보를_생성한다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_등록한다(등록할_주문_정보);

            // then
            주문_유형을_입력하지_않아서_주문_등록에_실패했다(response);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 주문_메뉴를_입력하지_않으면_베딜주문_등록에_실패한다(List<OrderLineItem> 비어있는_주문_메뉴) {
            // given
            Order 등록할_주문_정보 = OrderTestFixture.create()
                    .changeId(null)
                    .changeType(OrderType.DELIVERY)
                    .changeOrderTable(null)
                    .changeDeliveryAddress("서울시 강남구")
                    .changeStatus(null)
                    .changeOrderLineItems(비어있는_주문_메뉴)
                    .getOrder();

            // when
            ExtractableResponse<Response> response = 주문을_등록한다(등록할_주문_정보);

            // then
            주문_메뉴를_입력하지_않아서_주문_등록에_실패했다(response);
        }

        @Test
        void 존재하지_않는_메뉴를_배달주문에_등록하면_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록되지_않은_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록할_주문_정보 = 등록할_배달주문_정보_생성한다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_등록한다(등록할_주문_정보);

            // then
            존재하지_않는_메뉴를_주문에_포함시켜_주문_등록에_실패했다(response);
        }

        @Test
        void 숨김_상태의_메뉴를_배달주문에_등록하면_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            메뉴를_숨김_상태로_변경한다(등록된_메뉴);
            Order 등록할_주문_정보 = 등록할_배달주문_정보_생성한다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_등록한다(등록할_주문_정보);

            // then
            숨김_상태의_메뉴를_주문에_포함시켜_주문_등록에_실패했다(response);
        }

        @Test
        void 배달_주소를_입력하지_않으면_배달주문_등록은_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록할_주문_정보 = 배달_주소가_없는_주문_정보를_생성한다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_등록한다(등록할_주문_정보);

            // then
            배달_주소를_입력하지_않아서_주문_등록에_실패했다(response);
        }
    }

    @Nested
    class 배달주문_수락_상태_변경_인수테스트 {
        @Test
        void 대기중인_배달주문_수락_상태로_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_수락_상태로_변경한다(등록된_배달주문);

            // then
            대기중인_주문을_수락_상태로_변경했다(response);
            배달기사_회사에_배달요청을_했다(kitchenridersClientDummy);
        }

        @Test
        void 대기_상태의_배달주문이_아니라면_수락_상태_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);
            주문을_수락_상태로_변경한다(등록된_배달주문);
            주문을_제공_상태로_변경한다(등록된_배달주문);

            // when
            ExtractableResponse<Response> response = 주문을_수락_상태로_변경한다(등록된_배달주문);

            // then
            대기_상태의_주문이_아니라서_주문_수락_상태_변경에_실패했다(response);
        }
    }

    @Nested
    class 배달주문_제공_상태_변경_인수테스트 {
        @Test
        void 수락_상태_배달주문을_제공_상태_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);
            주문을_수락_상태로_변경한다(등록된_배달주문);

            // when
            ExtractableResponse<Response> response = 주문을_제공_상태로_변경한다(등록된_배달주문);

            // then
            수락_상태인_주문을_제공_상태로_변경에_성공했다(response);
        }

        @Test
        void 수락_상태가_아닌_배달주문은_제공_상태_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 주문을_제공_상태로_변경한다(등록된_배달주문);

            // then
            수락_상태의_주문이_아니라서_제공_상태로_변경에_실패했다(response);
        }
    }

    @Nested
    class 배달주문_배달시작_상태_변경_인수테스트 {
        @Test
        void 제공_상태_배달주문을_배달시작_상태_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);
            주문을_수락_상태로_변경한다(등록된_배달주문);
            주문을_제공_상태로_변경한다(등록된_배달주문);

            // when
            ExtractableResponse<Response> response = 배달주문을_배달시작_상태로_변경한다(등록된_배달주문);

            // then
            제공_상태인_주문을_배달시작_상태로_변경에_성공했다(response);
        }

        @Test
        void 제공_상태가_아닌_배달주문은_배달시작_상태_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 배달주문을_배달시작_상태로_변경한다(등록된_배달주문);

            // then
            제공_상태가_아닌_주문이라서_배달시작_상태로_변경에_실패했다(response);
        }
    }

    @Nested
    class 배달주문_배달완료_상태_변경_인수테스트 {
        @Test
        void 배달시작_상태_배달주문을_배달완료_상태_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);
            주문을_수락_상태로_변경한다(등록된_배달주문);
            주문을_제공_상태로_변경한다(등록된_배달주문);
            배달주문을_배달시작_상태로_변경한다(등록된_배달주문);

            // when
            ExtractableResponse<Response> response = 배달주문을_배달완료_상태로_변경한다(등록된_배달주문);

            // then
            배달시작_상태인_주문을_배달완료_상태로_변경에_성공했다(response);
        }

        @Test
        void 배달시작_상태가_아닌_배달주문은_배달완료_상태_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 배달주문을_배달시작_상태로_변경한다(등록된_배달주문);

            // then
            배달시작_상태가_아닌_주문이라서_배달완료_상태로_변경에_실패했다(response);
        }
    }

    @Nested
    class 배달주문_주문완료_상태_변경_인수테스트 {
        @Test
        void 배달완료_상태_배달주문을_주문완료_상태_변경에_성공한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);
            주문을_수락_상태로_변경한다(등록된_배달주문);
            주문을_제공_상태로_변경한다(등록된_배달주문);
            배달주문을_배달시작_상태로_변경한다(등록된_배달주문);
            배달주문을_배달완료_상태로_변경한다(등록된_배달주문);

            // when
            ExtractableResponse<Response> response = 주문을_주문완료_상태로_변경한다(등록된_배달주문);

            // then
            배달완료_상태인_배달주문을_주문완료_상태로_변경에_성공했다(response);
        }

        @Test
        void 배달완료_상태가_아닌_배달주문은_주문완료_상태_변경에_실패한다() {
            // given
            MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
            Product 등록된_상품 = 상품이_등록된_상태다();
            Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);
            Order 등록된_배달주문 = 배달주문이_등록된_상태다(등록된_메뉴);

            // when
            ExtractableResponse<Response> response = 배달주문을_배달시작_상태로_변경한다(등록된_배달주문);

            // then
            배달완료_상태가_아닌_배달주문이라서_주문완료_상태로_변경에_실패했다(response);
        }
    }
}
