package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient 배달요청;

    @DisplayName(value = "주문을 등록할 수 있다")
    @Test
    void create_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문을 등록할때 반드시 주문형태를 선택해야 한다")
    @Test
    void create_fail_should_contain_type() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문은 반드시 하나 이상의 주문구성메뉴를 포함하고 있어야 한다")
    @Test
    void create_fail_should_contain_orderLineItem() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문의 갯수와 주문구성메뉴의 갯수가 다를 수 없다")
    @Test
    void create_fail_menuList_size_should_same_orderLineItemList_size() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문구성메뉴의 갯수(quantity)는 0 이상이어야 한다")
    @Test
    void create_fail_orderLineItem_quantity_should_gt_0() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "판매중인 메뉴만 주문할 수 있다")
    @Test
    void create_fail_menu_should_display() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴의 가격과 주문구성메뉴의 가격이 다를 수 없다")
    @Test
    void create_fail_menu_price_should_same_orderLineItem_price() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 배달인 경우 반드시 주문주소를 포함하고 있어야 한다")
    @Test
    void create_fail_when_type_delivery_should_contain_delivery_address() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 매장식사인 경우 반드시 주문 테이블을 포함하고 있어야 한다")
    @Test
    void create_fail_when_type_eat_in_should_contain_order_table() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "공석이 아닌 주문테이블에 주문을 등록할 수 없다")
    @Test
    void create_fail_table_should_empty() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태를 주문수락으로 변경할 수 있다")
    @Test
    void accept_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태가 수락대기인 경우만 주문수락으로 변경할 수 있다")
    @Test
    void accept_status_should_waiting() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태를 서빙완료로 변경할 수 있다")
    @Test
    void serve_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태가 주문수락인 경우만 서빙완료로 변경 한다")
    @Test
    void serve_status_should_accept() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태를 배달중으로 변경할 수 있다")
    @Test
    void startDelivery_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태가 서빙완료인 경우만 배달중으로 변경한다")
    @Test
    void startDelivery_status_should_serve() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 배달인 경우만 배달중으로 변경한다")
    @Test
    void startDelivery_type_should_delivery() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태를 배달완료로 변경할 수 있다")
    @Test
    void completeDelivery_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태가 배달중인 경우만 배달완료로 변경한다")
    @Test
    void completeDelivery_status_should_startDelivery() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문상태를 주문종결로 변경할 수 있다")
    @Test
    void complete_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 배달인 경우, 주문상태가 배달중인 경우만 주문종결로 변경한다")
    @Test
    void complete_when_type_delivery_status_should_delivering() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 매장식사 또는 테이크아웃인경우, 주문상태가 서빙완료인경우만 주문종결로 변경한다")
    @Test
    void complete_when_type_eat_in_or_take_out_status_should_serve() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "전체 주문을 조회할 수 있다")
    @Test
    void findAll_success() throws Exception {
        //given

        //when

        //then
    }
}