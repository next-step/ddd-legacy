package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptance.acceptance_step.OrderTableStep;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.test_fixture.OrderTableTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static kitchenpos.acceptance.acceptance_step.MenuGroupStep.메뉴_그룹_등록된_상태다;
import static kitchenpos.acceptance.acceptance_step.MenuStep.메뉴가_등록된_상태다;
import static kitchenpos.acceptance.acceptance_step.OrderStep.주문_테이블에_주문을_등록한다;
import static kitchenpos.acceptance.acceptance_step.OrderTableStep.*;
import static kitchenpos.acceptance.acceptance_step.ProductStep.상품이_등록된_상태다;

@DisplayName("주문 테이블 인수 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderTableAcceptanceTest extends AcceptanceTestBase {

    @Test
    void 주문_테이블_등록에_성공한다() {
        // given
        OrderTable orderTable = OrderTableTestFixture.create()
                .changeId(null)
                .changeNumberOfGuests(0)
                .changeName("테이블1")
                .changeOccupied(false)
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_등록한다(orderTable);

        // then
        주문_테이블_등록됐다(response, "테이블1", 0, false);
    }

    @Test
    void 주문_테이블_등록_시_이름을_설정하지_않으면_등록에_실패한다() {
        // given
        OrderTable orderTable = OrderTableTestFixture.create()
                .changeId(null)
                .changeNumberOfGuests(0)
                .changeName(null)
                .changeOccupied(false)
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_등록한다(orderTable);

        // then
        주문_테이블_등록에_실패한다(response);
    }

    @Test
    void 주문_테이블_등록_시_이름이_빈_문자열이면_등록에_실패한다() {
        // given
        OrderTable orderTable = OrderTableTestFixture.create()
                .changeId(null)
                .changeNumberOfGuests(0)
                .changeName("")
                .changeOccupied(false)
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_등록한다(orderTable);

        // then
        주문_테이블_등록에_실패한다(response);
    }

    @Test
    void 주문_테이블_새로_등록_시_손님수0명_그리고_비어있는_상태로_등록된다() {
        // given
        OrderTable orderTable = OrderTableTestFixture.create()
                .changeId(null)
                .changeNumberOfGuests(100)
                .changeName("테이블1")
                .changeOccupied(true)
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_등록한다(orderTable);

        // then
        주문_테이블_등록됐다(response, "테이블1", 0, false);
    }

    @Test
    void 주문_테이블을_손님이_앉은_상태로_변경에_성공한다() {
        // given
        OrderTable orderTable = 주문_테이블_등록된_상태다();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_손님이_앉은_상태로_변경한다(orderTable);

        // then
        주문_테이블_손님이_앉은_상태로_변경에_성공한다(response);
    }

    @Test
    void 등록되지않은_주문_테이블이면_손님이_앉은_상태로_변경에_실패한다() {
        // given
        OrderTable 등록_안된_테이블 = 주문_테이블이_등록된_상태가_아니다();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_손님이_앉은_상태로_변경한다(등록_안된_테이블);

        // then
        OrderTableStep.주문_테이블을_손님이_앉은_상태로_변경에_실패한다(response);
    }

    @Test
    void 주문_테이블을_비어있는_상태로_변경에_성공한다() {
        // given
        OrderTable orderTable = 주문_테이블_등록된_상태다();
        주문_테이블을_손님이_앉은_상태로_변경한다(orderTable);

        // when
        ExtractableResponse<Response> response = 주문_테이블을_비어있는_상태로_변경한다(orderTable);

        // then
        주문_테이블을_비어있는_상태로_변경에_성공(response);
    }

    @Test
    void 등록되지않은_주문_테이블이면_비어있는_상태로_변경에_실패한다() {
        // given
        OrderTable orderTable = 주문_테이블이_등록된_상태가_아니다();

        // when
        ExtractableResponse<Response> response = 주문_테이블을_비어있는_상태로_변경한다(orderTable);

        // then
        주문_테이블을_비어있는_상태로_변경에_실패한다(response);
    }

    @Test
    void 주문_테이블에_등록된_주문이_완료_상태가_아니라면_비어있는_상태로_변경에_실패한다() {
        // given
        MenuGroup 등록된_메뉴_그룹 = 메뉴_그룹_등록된_상태다();
        Product 등록된_상품 = 상품이_등록된_상태다();
        Menu 등록된_메뉴 = 메뉴가_등록된_상태다(등록된_상품, 등록된_메뉴_그룹);

        OrderTable 등록된_주문_테이블 = 주문_테이블_등록된_상태다();
        주문_테이블을_손님이_앉은_상태로_변경한다(등록된_주문_테이블);
        주문_테이블에_주문을_등록한다(등록된_메뉴, 등록된_주문_테이블);

        // when
        ExtractableResponse<Response> response = 주문_테이블을_비어있는_상태로_변경한다(등록된_주문_테이블);

        // then
        주문_테이블에_주문이_완료되지않아_비어있는_상태로_변경에_실패(response);
    }

    @Test
    void 주문_테이블에_앉은_고객_수_변경에_성공한다() {
        // given
        OrderTable 등록된_주문_테이블 = 주문_테이블_등록된_상태다();
        주문_테이블을_손님이_앉은_상태로_변경한다(등록된_주문_테이블);
        OrderTable 변경할_손님_수_정보 = OrderTableTestFixture.create()
                .changeId(등록된_주문_테이블.getId())
                .changeName(등록된_주문_테이블.getName())
                .changeNumberOfGuests(10)
                .changeOccupied(등록된_주문_테이블.isOccupied())
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블에_앉은_손님_수를_변경한다(변경할_손님_수_정보);

        // then
        주문_테이블에_앉은_고객_수_변경에_성공했다(response, 10);
    }

    @Test
    void 주문_테이블에_앉은_고객_수_변경_시_고객_수는_0보다_작을_수_없다() {
        // given
        OrderTable 등록된_주문_테이블 = 주문_테이블_등록된_상태다();
        주문_테이블을_손님이_앉은_상태로_변경한다(등록된_주문_테이블);
        OrderTable 변경할_손님_수_정보 = OrderTableTestFixture.create()
                .changeId(등록된_주문_테이블.getId())
                .changeName(등록된_주문_테이블.getName())
                .changeNumberOfGuests(-1)
                .changeOccupied(등록된_주문_테이블.isOccupied())
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블에_앉은_손님_수를_변경한다(변경할_손님_수_정보);

        // then
        주문_테이블에_앉은_고객_수_변경에_실패했다(response);
    }

    @Test
    void 등록되지않은_주문_테이블이면_주문_테이블에_앉은_고객_수_변경에_실패한다() {
        //given
        OrderTable 주문_테이블이_등록된_상태가_아니다 = 주문_테이블이_등록된_상태가_아니다();
        OrderTable 변경할_손님_수_정보 = OrderTableTestFixture.create()
                .changeId(주문_테이블이_등록된_상태가_아니다.getId())
                .changeName(주문_테이블이_등록된_상태가_아니다.getName())
                .changeNumberOfGuests(10)
                .changeOccupied(주문_테이블이_등록된_상태가_아니다.isOccupied())
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블에_앉은_손님_수를_변경한다(변경할_손님_수_정보);

        // then
        주문_테이블이_등록되지_않아_앉은_고객_수_변경에_실패했다(response);
    }

    @Test
    void 비어있는_주문_테이블에_앉은_고객_수_변경_시_변경에_실패한다() {
        // given
        OrderTable 등록된_주문_테이블 = 주문_테이블_등록된_상태다();
        OrderTable 변경할_손님_수_정보 = OrderTableTestFixture.create()
                .changeId(등록된_주문_테이블.getId())
                .changeName(등록된_주문_테이블.getName())
                .changeNumberOfGuests(10)
                .changeOccupied(등록된_주문_테이블.isOccupied())
                .getOrderTable();

        // when
        ExtractableResponse<Response> response = 주문_테이블에_앉은_손님_수를_변경한다(변경할_손님_수_정보);

        // then
        주문_테이블에_앉은_고객_수_변경에_실패했다(response);
    }

    @Test
    void 등록된_전체_주문_테이블_정보_조회에_성공한다() {
        // given
        주문_테이블_등록된_상태다();
        주문_테이블_등록된_상태다();
        주문_테이블_등록된_상태다();

        // when
        ExtractableResponse<Response> response = 등록된_전체_주문_테이블_정보를_조회한다();

        // then
        등록된_전체_주문_테이블_정보를_조회에_성공했다(response);
    }
}
