package kitchenpos.order.accpetance;

import io.restassured.common.mapper.TypeRef;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.menugroup.fixture.MenuGroupFixture;
import kitchenpos.ordertable.fixture.OrderTableFixture;
import kitchenpos.product.fixture.ProductFixture;
import kitchenpos.support.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.menu.acceptance.step.MenuStep.메뉴를_등록한다;
import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹을_등록한다;
import static kitchenpos.order.accpetance.step.OrderStep.음식을_전달한다;
import static kitchenpos.order.accpetance.step.OrderStep.주문_목록을_조회한다;
import static kitchenpos.order.accpetance.step.OrderStep.주문을_등록한다;
import static kitchenpos.order.accpetance.step.OrderStep.주문을_수락한다;
import static kitchenpos.order.accpetance.step.OrderStep.주문을_완료한다;
import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블에_앉다;
import static kitchenpos.ordertable.acceptance.step.OrderTableStep.테이블을_등록한다;
import static kitchenpos.product.acceptance.step.ProductStep.제품을_등록한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class EatInOrderAcceptanceTest extends AcceptanceTest {

    private static final String ORDER_ID_KEY = "id";

    private Menu 김치찜_1인_메뉴, 봉골레_파스타_세트_메뉴;
    private OrderTable 테이블_1;

    @BeforeEach
    void dataload() {
        김치찜_1인_메뉴 = 김치찜_1인_메뉴를_등록한다();
        봉골레_파스타_세트_메뉴 = 봉골레_파스타_세트_메뉴를_등록한다();

        테이블_1 = 테이블을_등록한다(OrderTableFixture.테이블_1).as(OrderTable.class);
        테이블에_앉다(테이블_1.getId());
    }

    /**
     * <pre>
     * when 매장주문을 등록하면
     * then 주문 목록 조회 시 등록한 주문을 찾을 수 있다.
     * then 해당 주문은 대기 상태이다.
     * then 해당 주문의 유형은 배달주문이다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 김치찜_1인_메뉴_1개_주문 = 주문_항목을_생성한다(김치찜_1인_메뉴, 1);
        var 봉골레_파스타_세트_메뉴_1개_주문 = 주문_항목을_생성한다(봉골레_파스타_세트_메뉴, 1);
        var 등록할_주문 = 주문을_생성한다(List.of(김치찜_1인_메뉴_1개_주문, 봉골레_파스타_세트_메뉴_1개_주문));

        var 등록된_주문_아이디 = 주문을_등록한다(등록할_주문)
                .jsonPath()
                .getUUID(ORDER_ID_KEY);

        // then
        var 주문_아이디_목록 = 주문_목록을_조회한다()
                .jsonPath()
                .getList(ORDER_ID_KEY, UUID.class);

        assertThat(주문_아이디_목록).containsExactly(등록된_주문_아이디);
    }

    /**
     * <pre>
     * given 김치찜 1인 메뉴 1개를 주문한다.
     * given 봉골레 파스타 세트 메뉴 1개를 주문한다.
     * when  주문 목록을 조회하면
     * then  등록한 2개의 주문을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        var 김치찜_1인_메뉴_1개_주문 = 주문_항목을_생성한다(김치찜_1인_메뉴, 1);
        var 봉골레_파스타_세트_메뉴_1개_주문 = 주문_항목을_생성한다(봉골레_파스타_세트_메뉴, 1);

        var 등록할_김치찜_1인_메뉴_1개_주문 = 주문을_생성한다(List.of(김치찜_1인_메뉴_1개_주문));
        var 등록할_봉골레_파스타_세트_메뉴_1개_주문 = 주문을_생성한다(List.of(봉골레_파스타_세트_메뉴_1개_주문));

        var 등록된_김치찜_1인_메뉴_1개_주문_아이디 = 주문을_등록한다(등록할_김치찜_1인_메뉴_1개_주문)
                .jsonPath()
                .getUUID(ORDER_ID_KEY);
        var 등록된_봉골레_파스타_세트_메뉴_1개_주문 = 주문을_등록한다(등록할_봉골레_파스타_세트_메뉴_1개_주문)
                .jsonPath()
                .getUUID(ORDER_ID_KEY);

        // when
        var 주문_아이디_목록 = 주문_목록을_조회한다()
                .jsonPath()
                .getList(ORDER_ID_KEY, UUID.class);

        // then
        assertThat(주문_아이디_목록)
                .containsExactly(등록된_김치찜_1인_메뉴_1개_주문_아이디, 등록된_봉골레_파스타_세트_메뉴_1개_주문);
    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * when  점주가 주문을 수락하면
     * then  주문 목록 조회 시 해당 주문 상태는 수락 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("수락")
    void accept() {
        // given
        var 등록된_주문_아이디 = 김치찜_1인_메뉴_1개_봉골레_세트_1개_주문을_등록한다().getId();

        // when
        주문을_수락한다(등록된_주문_아이디);

        // then
        var 주문_목록 = 주문_목록을_조회한다().as(new TypeRef<List<Order>>() {});
        var 주문_optional = 주문_목록.stream().filter(order -> Objects.equals(order.getId(), 등록된_주문_아이디))
                .findFirst();

        assertAll(
                () -> assertThat(주문_optional.isPresent()).isTrue(),
                () -> assertThat(주문_optional.get().getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        );
    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * given 점주가 주문을 수락하고 조리가 완료되어
     * when  테이블에 음식을 전달하면
     * then  주문 목록 조회 시 해당 주문 상태는 전달 상태이다.
     * </pre>
     */
    @Test
    @DisplayName("전달")
    void serve() {
        // given
        var 등록된_주문_아이디 = 김치찜_1인_메뉴_1개_봉골레_세트_1개_주문을_등록한다().getId();

        주문을_수락한다(등록된_주문_아이디);

        // when
        음식을_전달한다(등록된_주문_아이디);

        // then
        var 주문_목록 = 주문_목록을_조회한다().as(new TypeRef<List<Order>>() {});
        var 주문_optional = 주문_목록.stream().filter(order -> Objects.equals(order.getId(), 등록된_주문_아이디))
                .findFirst();

        assertAll(
                () -> assertThat(주문_optional.isPresent()).isTrue(),
                () -> assertThat(주문_optional.get().getStatus()).isEqualTo(OrderStatus.SERVED)
        );
    }

    /**
     * <pre>
     * given 손님이 주문을 등록하고
     * given 점주가 주문을 수락하고 조리가 완료되어
     * given 테이블에 음식을 전달하여
     * when  주문이 완료되면
     * then  주문 목록 조회 시 해당 주문 상태는 완료 상태이다.
     * then  테이블에 다른 손님이 앉을 수 있다.
     * then  테이블은 비워진다.
     * </pre>
     */
    @Test
    @DisplayName("완료")
    void complete() {
        // given
        var 등록된_주문_아이디 = 김치찜_1인_메뉴_1개_봉골레_세트_1개_주문을_등록한다().getId();

        주문을_수락한다(등록된_주문_아이디);
        음식을_전달한다(등록된_주문_아이디);

        // when
        주문을_완료한다(등록된_주문_아이디);

        // then
        var 주문_목록 = 주문_목록을_조회한다().as(new TypeRef<List<Order>>() {});
        var 주문_optional = 주문_목록.stream().filter(order -> Objects.equals(order.getId(), 등록된_주문_아이디))
                .findFirst();

        assertAll(
                () -> assertThat(주문_optional.isPresent()).isTrue(),
                () -> assertThat(주문_optional.get().getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(주문_optional.get().getOrderTable().isOccupied()).isFalse(),
                () -> assertThat(주문_optional.get().getOrderTable().getNumberOfGuests()).isEqualTo(0)
        );
    }

    private Menu 김치찜_1인_메뉴를_등록한다() {
        var 한식_그룹 = 메뉴_그룹을_등록한다(MenuGroupFixture.한식).as(MenuGroup.class);

        var 김치찜 = 제품을_등록한다(ProductFixture.김치찜).as(Product.class);
        var 공기밥 = 제품을_등록한다(ProductFixture.공기밥).as(Product.class);

        var 김치찜_1개 = 메뉴_제품을_생성한다(김치찜, 1L);
        var 공기밥_1개 = 메뉴_제품을_생성한다(공기밥, 1L);
        var 메뉴_제품_목록 = List.of(김치찜_1개, 공기밥_1개);

        var 등록할_김치찜_1인_메뉴 = 메뉴를_생성한다(
                "김치찜 1인",
                new BigDecimal(24_000),
                한식_그룹,
                true,
                메뉴_제품_목록
        );

        return 메뉴를_등록한다(등록할_김치찜_1인_메뉴).as(Menu.class);
    }

    private Menu 봉골레_파스타_세트_메뉴를_등록한다() {
        var 양식_그룹 = 메뉴_그룹을_등록한다(MenuGroupFixture.양식).as(MenuGroup.class);

        var 봉골레_파스타 = 제품을_등록한다(ProductFixture.봉골레_파스타).as(Product.class);
        var 수제_마늘빵 = 제품을_등록한다(ProductFixture.수제_마늘빵).as(Product.class);

        var 봉골레_파스타_1개 = 메뉴_제품을_생성한다(봉골레_파스타, 1L);
        var 수제_마늘빵_3개 = 메뉴_제품을_생성한다(수제_마늘빵, 3L);

        var 등록할_봉골레_파스타_세트_메뉴 = 메뉴를_생성한다(
                "봉골레 파스타 세트",
                new BigDecimal(15_700),
                양식_그룹,
                true,
                List.of(봉골레_파스타_1개, 수제_마늘빵_3개)
        );

        return 메뉴를_등록한다(등록할_봉골레_파스타_세트_메뉴).as(Menu.class);
    }

    private MenuProduct 메뉴_제품을_생성한다(Product product, Long quantity) {
        var 메뉴_제품 = new MenuProduct();
        메뉴_제품.setProductId(product.getId());
        메뉴_제품.setProduct(product);
        메뉴_제품.setQuantity(quantity);

        return 메뉴_제품;
    }

    private Menu 메뉴를_생성한다(
            String name,
            BigDecimal price,
            MenuGroup menuGroup,
            boolean displayed,
            List<MenuProduct> products) {
        var 메뉴 = new Menu();
        메뉴.setName(name);
        메뉴.setPrice(price);
        메뉴.setMenuGroupId(menuGroup.getId());
        메뉴.setMenuGroup(menuGroup);
        메뉴.setDisplayed(displayed);
        메뉴.setMenuProducts(products);

        return 메뉴;
    }

    private OrderLineItem 주문_항목을_생성한다(Menu menu, long quantity) {
        var 주문항목 = new OrderLineItem();
        주문항목.setMenuId(menu.getId());
        주문항목.setMenu(menu);
        주문항목.setQuantity(quantity);
        주문항목.setPrice(menu.getPrice());

        return 주문항목;
    }

    private Order 주문을_생성한다(List<OrderLineItem> orderLineItems) {
        var 주문 = new Order();
        주문.setType(OrderType.EAT_IN);
        주문.setOrderLineItems(orderLineItems);
        주문.setOrderTableId(테이블_1.getId());
        주문.setOrderTable(테이블_1);

        return 주문;
    }

    private Order 김치찜_1인_메뉴_1개_봉골레_세트_1개_주문을_등록한다() {
        var 김치찜_1인_메뉴_1개_주문 = 주문_항목을_생성한다(김치찜_1인_메뉴, 1);
        var 봉골레_파스타_세트_메뉴_1개_주문 = 주문_항목을_생성한다(봉골레_파스타_세트_메뉴, 1);
        var 주문_항목 = List.of(김치찜_1인_메뉴_1개_주문, 봉골레_파스타_세트_메뉴_1개_주문);
        var 등록할_주문 = 주문을_생성한다(주문_항목);

        return 주문을_등록한다(등록할_주문).as(Order.class);
    }

}
