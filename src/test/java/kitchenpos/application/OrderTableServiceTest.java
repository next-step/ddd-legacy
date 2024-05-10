package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupTestHelper;
import kitchenpos.helper.MenuProductTestHelper;
import kitchenpos.helper.MenuTestHelper;
import kitchenpos.helper.OrderTableTestHelper;
import kitchenpos.helper.OrderTestHelper;
import kitchenpos.helper.ProductTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Transactional
class OrderTableServiceTest extends SetupTest{

    @Autowired
    private OrderTableService orderTableService;

    private OrderTable 빈주문테이블;
    private OrderTable 사용중인_주문테이블;

    private List<OrderTable> 주문테이블들 = new ArrayList<>();
    private Order 대기중_주문;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        빈주문테이블 = OrderTableTestHelper.주문테이블_생성("빈주문테이블");
        사용중인_주문테이블 = OrderTableTestHelper.사용중인_주문테이블_생성("사용중인_주문테이블", 4);

        주문테이블들.add(빈주문테이블);
        주문테이블들.add(사용중인_주문테이블);
        대기중_주문 = this.makeSampleOrder(사용중인_주문테이블);
    }

    private Order makeSampleOrder(OrderTable 사용중인_주문테이블) {
        Product 마라탕 = ProductTestHelper.음식_생성("마라탕", BigDecimal.valueOf(10000));
        Product 미니꿔바로우 = ProductTestHelper.음식_생성("미니꿔바로우", BigDecimal.valueOf(8000));
        Product 콜라 = ProductTestHelper.음식_생성("콜라", BigDecimal.valueOf(3000));

        MenuProduct 마라탕메뉴 = MenuProductTestHelper.음식메뉴_생성(마라탕, 1);
        MenuProduct 미니꿔바로우메뉴 = MenuProductTestHelper.음식메뉴_생성(미니꿔바로우, 1);
        MenuProduct 콜라메뉴 = MenuProductTestHelper.음식메뉴_생성(콜라, 1);

        MenuGroup 추천메뉴 = MenuGroupTestHelper.메뉴카테고리_생성("추천메뉴");

        Menu 마라세트 = MenuTestHelper.메뉴_생성(추천메뉴, "마라세트", BigDecimal.valueOf(16000), Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴), true);
        Menu 나홀로세트 = MenuTestHelper.메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(11000), Arrays.asList(마라탕메뉴, 콜라메뉴), true);

        OrderLineItem 마라세트_주문 = new OrderLineItem();
        마라세트_주문.setMenuId(마라세트.getId());
        마라세트_주문.setMenu(마라세트);
        마라세트_주문.setQuantity(1);
        마라세트_주문.setPrice(마라세트.getPrice());

        OrderLineItem 나홀로세트_주문 = new OrderLineItem();
        나홀로세트_주문.setMenuId(나홀로세트.getId());
        나홀로세트_주문.setMenu(나홀로세트);
        나홀로세트_주문.setQuantity(2);
        나홀로세트_주문.setPrice(나홀로세트.getPrice());

        List<OrderLineItem> 주문할_메뉴들 = Arrays.asList(마라세트_주문, 나홀로세트_주문);

        return OrderTestHelper.대기_주문_생성(OrderType.EAT_IN, 주문할_메뉴들, 사용중인_주문테이블);
    }

    @DisplayName("주문테이블을 생성하다.")
    @Test
    void createOfOrderTable(){
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setName("주문테이블1");

        OrderTable createOrderTable = orderTableService.create(requestOrderTable);
        assertThat(createOrderTable.getName()).isSameAs(requestOrderTable.getName());
    }

    @DisplayName("주문테이블 생성때 이름을 입력하지 않을 경우 IllegalArgumentException 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createOfEmptyNameOrderTable(String name){
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(requestOrderTable));
    }

    @DisplayName("특정 주문테이블에 손님이 앉다.")
    @Test
    void sitOfOrderTable(){
        OrderTable sitOrderTable = orderTableService.sit(빈주문테이블.getId());
        assertThat(sitOrderTable.isOccupied()).isSameAs(true);
    }

    @DisplayName("등록되지 않은 주문테이블에 손님을 앉힐 경우 NoSuchElementException 예외가 발생한다.")
    @Test
    void sitOfNoOrderTable(){
        OrderTable 없는주문테이블 = new OrderTable();
        없는주문테이블.setId(UUID.randomUUID());

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.sit(없는주문테이블.getId()));
    }


    @DisplayName("특정 주문테이블을 치우다.")
    @Test
    void clearOrderTable(){
        Order 주문완료_주문 = OrderTestHelper.생성한_주문_상태_변경(대기중_주문.getId(), OrderStatus.COMPLETED);
        OrderTable 주문완료테이블 = 주문완료_주문.getOrderTable();

        OrderTable clearOrderTable = orderTableService.clear(주문완료테이블.getId());
        assertThat(clearOrderTable.isOccupied()).isSameAs(false);
        assertThat(clearOrderTable.getNumberOfGuests()).isSameAs(0);
    }

    @DisplayName("등록되지 않은 주문테이블을 치우려고 할 경우 NoSuchElementException 예외가 발생한다.")
    @Test
    void clearNoOrderTable(){
        OrderTable 없는주문테이블 = new OrderTable();
        없는주문테이블.setId(UUID.randomUUID());

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.clear(없는주문테이블.getId()));
    }

    @DisplayName("아직 주문이 완료되지 않은 주문테이블을 치우려고 할 경우 IllegalStateException 예외가 발생한다.")
    @Test
    void clearOrderTableOfNoCompletedOrder(){
        OrderTable 대기중_주문테이블 = 대기중_주문.getOrderTable();

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(대기중_주문테이블.getId()));
    }

    @DisplayName("특정 주문테이블의 사용인원수를 변경하다.")
    @Test
    void changeNumberOfGuestsOfOrderTable(){
        final int changeNumberOfGuest = 10;
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setNumberOfGuests(changeNumberOfGuest);

        OrderTable changeNumberOrderTable = orderTableService.changeNumberOfGuests(사용중인_주문테이블.getId(), requestOrderTable);
        assertThat(changeNumberOrderTable.getNumberOfGuests()).isSameAs(changeNumberOfGuest);
    }

    @DisplayName("특정 주문테이블의 변경할 사용인원수를 음수로 입력하면 IllegalArgumentException 예외가 발생한다.")
    @Test
    void changeMinusNumberOfGuestsOfOrderTable(){
        OrderTable requestOrderTable = new OrderTable();
        requestOrderTable.setNumberOfGuests(-1);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(사용중인_주문테이블.getId(), requestOrderTable));
    }

    @DisplayName("모든 주문테이블를 조회한다.")
    @Test
    void findAllOfOrderTable(){
        List<OrderTable> orderTables = orderTableService.findAll();
        assertThat(주문테이블들.size()).isSameAs(orderTables.size());
    }

}