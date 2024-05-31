package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixtures.Fixture;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {
    @Mock private OrderRepository orderRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private OrderTableRepository orderTableRepository;
    @Mock private KitchenridersClient kitchenridersClient;
    @InjectMocks private OrderService orderService;

    @DisplayName("주문을 하기 위해 주문 타입을 선택해야 한다.")
    @Test
    void case1() {
        final Order order = Fixture.fixtureOrder();
        final Menu menu = order.getOrderLineItems().get(0).getMenu();
        final OrderTable orderTable = order.getOrderTable();

        order.setType(OrderType.DELIVERY);
        BDDMockito.given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        BDDMockito.given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.ofNullable(orderTable));
        BDDMockito.given(orderRepository.save(any())).willReturn(order);

        final Order actual = orderService.create(order);
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("주문을 하기 위해 주문 정보를 입력해야 한다.")
    @Test
    void case2() {

    }

    @DisplayName("주문 정보를 입력하기 위해 메뉴가 존재해야 하며, 금액과 수량을 입력해야 한다.")
    @Test
    void case3() {

    }

    @DisplayName("주문을 하기 위해 주문 정보에 메뉴가 한 개 이상 존재해야 한다.")
    @Test
    void case4() {

    }

    @DisplayName("주문을 하기 위해 해당 메뉴의 상태가 노출돼야 한다.")
    @Test
    void case5() {

    }

    @DisplayName("주문을 하기 위해 주문 정보에 금액과 메뉴 금액이 같아야 한다.")
    @Test
    void case6() {

    }

    @DisplayName("주문 타입이 배달(DELIVERY)일 경우 주문이 들어와 있어야 하며, 주소지를 입력해야 한다.")
    @Test
    void case7() {

    }

    @DisplayName("주문 타입이 먹고가기(EAT_IN)일 경우 주문테이블에 고객이 앉아있어야 하며 주문 수량은 1개 이상이어야 한다.")
    @Test
    void case8() {

    }

    @DisplayName("주문을 할 경우 주문 상태가 대기중(WAITING) 상태가 된다.")
    @Test
    void case9() {

    }

    @DisplayName("주문을 접수(ACCEPTED)하기 위해 주문이 있어야 하고, 주문 타입이 대기중(WAITING)이여야 한다.")
    @Test
    void case10() {

    }

    @DisplayName("주문을 접수(ACCEPTED)하기 위해 주문 타입이 배달(DELIVERY)이면 주문 정보와 총 금액, 배달지 주소를 배달기사에게 전달한다.")
    @Test
    void case11() {

    }

    @DisplayName("주문을 전달(SERVED)하기 위해 주문 정보가 있어야 하고, 주문 타입이 접수(ACCEPTED)이여야 한다.")
    @Test
    void case12() {

    }

    @DisplayName("주문을 배달중(DELIVERING)하기 위해 주문 정보가 있어야 하고, 주문 타입이 배달(DELIVERY)하고 주문 상태가 전달(SERVED)여야 한다.")
    @Test
    void case13() {

    }

    @DisplayName("주문을 배달(DELIVERED)하기 위해 주문 정보가 있어야 하고, 주문 타입이 배달중(DELIVERING)여야 한다.")
    @Test
    void case14() {

    }

    @DisplayName("주문을 완료(COMPLETED)하기 위해 주문 정보가 있어야 하고, 주문 타입이 배달중(DELIVERING), 그리고 주문 상태는 배달(DELIVERED)이여야 한다.")
    @Test
    void case15() {

    }

    @DisplayName("주문을 완료(COMPLETED)하기 위해 주문 정보가 있어야 하고, 주문 타입이 가져가기(TAKEOUT) 또는 먹고가기(EAT_IN), 그리고 주문 상태는 전달(SERVED)이여야 한다.")
    @Test
    void case16() {

    }

    @DisplayName("주문을 완료(COMPLETED)하게 되면, 고객이 음식을 먹고 갔기 때문에 주문테이블이 비어있어야 한다.")
    @Test
    void case17() {

    }

    @DisplayName("주문을 전체 조회 할 수 있다.")
    @Test
    void case18() {

    }

}
