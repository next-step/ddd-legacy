package kitchenpos.application

import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderType
import kitchenpos.infra.KitchenridersClient
import kitchenpos.order.OrderFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @MockBean
    private lateinit var kitchenridersClient: KitchenridersClient

    @Test
    @DisplayName("배달주문 접수를 할 때 배달라이더에게 배달요청을 보낸다")
    @Transactional
    fun requestDelivery() {
        // given
        val order = OrderFixture.fixture(type = OrderType.DELIVERY)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when
        orderService.accept(order.id)

        // then
        then(kitchenridersClient).should(times(1)).requestDelivery(any(), any(), any())
    }

    @CsvSource(
        "DELIVERY, WAITING",
        "TAKEOUT, WAITING",
        "EAT_IN, WAITING",
    )
    @DisplayName("주문대기 / 주문접수로 변경 / 성공")
    @ParameterizedTest
    fun acceptSuccess(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when
        orderService.accept(order.id)

        // then
        val acceptedOrder = orderRepository.findById(order.id).get()
        assertThat(acceptedOrder.status).isEqualTo(OrderStatus.ACCEPTED)
    }


    @CsvSource(
//        "DELIVERY, WAITING",
//        "TAKEOUT, WAITING",
//        "EAT_IN, WAITING",
        "DELIVERY, ACCEPTED",
        "TAKEOUT, ACCEPTED",
        "EAT_IN, ACCEPTED",
        "DELIVERY, SERVED",
        "TAKEOUT, SERVED",
        "EAT_IN, SERVED",
        "DELIVERY, DELIVERING",
        "TAKEOUT, DELIVERING",
        "EAT_IN, DELIVERING",
        "DELIVERY, DELIVERED",
        "TAKEOUT, DELIVERED",
        "EAT_IN, DELIVERED",
        "DELIVERY, COMPLETED",
        "TAKEOUT, COMPLETED",
        "EAT_IN, COMPLETED",
    )
    @DisplayName("주문대기 아님 / 주문접수로 변경 / 실패")
    @ParameterizedTest
    fun acceptFail(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when then
        assertThatThrownBy {
            orderService.accept(order.id)
        }
    }

    @CsvSource(
        "DELIVERY, ACCEPTED",
        "TAKEOUT, ACCEPTED",
        "EAT_IN, ACCEPTED",
    )
    @DisplayName("주문접수 / 주문준비완료로 변경 / 성공")
    @ParameterizedTest
    fun servedSuccess(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when
        orderService.serve(order.id)

        // then
        val acceptedOrder = orderRepository.findById(order.id).get()
        assertThat(acceptedOrder.status).isEqualTo(OrderStatus.SERVED)
    }


    @CsvSource(
        "DELIVERY, WAITING",
        "TAKEOUT, WAITING",
        "EAT_IN, WAITING",
//        "DELIVERY, ACCEPTED",
//        "TAKEOUT, ACCEPTED",
//        "EAT_IN, ACCEPTED",
        "DELIVERY, SERVED",
        "TAKEOUT, SERVED",
        "EAT_IN, SERVED",
        "DELIVERY, DELIVERING",
        "TAKEOUT, DELIVERING",
        "EAT_IN, DELIVERING",
        "DELIVERY, DELIVERED",
        "TAKEOUT, DELIVERED",
        "EAT_IN, DELIVERED",
        "DELIVERY, COMPLETED",
        "TAKEOUT, COMPLETED",
        "EAT_IN, COMPLETED",
    )
    @DisplayName("주문접수 아님 / 주문준비완료로 변경 / 실패")
    @ParameterizedTest
    fun servedFail(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when then
        assertThatThrownBy {
            orderService.serve(order.id)
        }
    }

    @CsvSource(
        "DELIVERY, SERVED",
    )
    @DisplayName("배달주문준비완료 / 배달중 으로 변경 / 성공")
    @ParameterizedTest
    fun startDeliverySuccess(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when
        orderService.startDelivery(order.id)

        // then
        val acceptedOrder = orderRepository.findById(order.id).get()
        assertThat(acceptedOrder.status).isEqualTo(OrderStatus.DELIVERING)
    }


    @CsvSource(
        "DELIVERY, WAITING",
        "TAKEOUT, WAITING",
        "EAT_IN, WAITING",
        "DELIVERY, ACCEPTED",
        "TAKEOUT, ACCEPTED",
        "EAT_IN, ACCEPTED",
//        "DELIVERY, SERVED",
        "TAKEOUT, SERVED",
        "EAT_IN, SERVED",
        "DELIVERY, DELIVERING",
        "TAKEOUT, DELIVERING",
        "EAT_IN, DELIVERING",
        "DELIVERY, DELIVERED",
        "TAKEOUT, DELIVERED",
        "EAT_IN, DELIVERED",
        "DELIVERY, COMPLETED",
        "TAKEOUT, COMPLETED",
        "EAT_IN, COMPLETED",
    )
    @DisplayName("배달주문준비완료 아님 / 배달중 으로 변경 / 실패")
    @ParameterizedTest
    fun startDeliveryFail(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when then
        assertThatThrownBy {
            orderService.startDelivery(order.id)
        }
    }

    @CsvSource(
        "DELIVERY, DELIVERING",
    )
    @DisplayName("배달주문배달중 / 배달완료로 변경 / 성공")
    @ParameterizedTest
    fun completeDeliverySuccess(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when
        orderService.completeDelivery(order.id)

        // then
        val acceptedOrder = orderRepository.findById(order.id).get()
        assertThat(acceptedOrder.status).isEqualTo(OrderStatus.DELIVERED)
    }


    @CsvSource(
        "DELIVERY, WAITING",
        "TAKEOUT, WAITING",
        "EAT_IN, WAITING",
        "DELIVERY, ACCEPTED",
        "TAKEOUT, ACCEPTED",
        "EAT_IN, ACCEPTED",
        "DELIVERY, SERVED",
        "TAKEOUT, SERVED",
        "EAT_IN, SERVED",
//        "DELIVERY, DELIVERING",
        "TAKEOUT, DELIVERING",
        "EAT_IN, DELIVERING",
        "DELIVERY, DELIVERED",
        "TAKEOUT, DELIVERED",
        "EAT_IN, DELIVERED",
        "DELIVERY, COMPLETED",
        "TAKEOUT, COMPLETED",
        "EAT_IN, COMPLETED",
    )
    @DisplayName("배달주문배달중 아님 / 배달완료 로 변경 / 실패")
    @ParameterizedTest
    fun completeDeliveryFail(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when then
        assertThatThrownBy {
            orderService.completeDelivery(order.id)
        }
    }

    @CsvSource(
        "DELIVERY, DELIVERED",
        "TAKEOUT, SERVED",
        "EAT_IN, SERVED",
    )
    @DisplayName("배달주문배달중 홀주문준비완료 포장주문준비완료 / 완료로 변경 / 성공")
    @ParameterizedTest
    fun completeSuccess(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when
        orderService.complete(order.id)

        // then
        val acceptedOrder = orderRepository.findById(order.id).get()
        assertThat(acceptedOrder.status).isEqualTo(OrderStatus.COMPLETED)
    }


    @CsvSource(
        "DELIVERY, WAITING",
        "TAKEOUT, WAITING",
        "EAT_IN, WAITING",
        "DELIVERY, ACCEPTED",
        "TAKEOUT, ACCEPTED",
        "EAT_IN, ACCEPTED",
        "DELIVERY, SERVED",
//        "TAKEOUT, SERVED",
//        "EAT_IN, SERVED",
        "DELIVERY, DELIVERING",
        "TAKEOUT, DELIVERING",
        "EAT_IN, DELIVERING",
//        "DELIVERY, DELIVERED",
        "TAKEOUT, DELIVERED",
        "EAT_IN, DELIVERED",
        "DELIVERY, COMPLETED",
        "TAKEOUT, COMPLETED",
        "EAT_IN, COMPLETED",
    )
    @DisplayName("배달주문배달중 홀주문준비완료 포장주문준비완료 아님 / 완료 로 변경 / 실패")
    @ParameterizedTest
    fun completeFail(type: OrderType, status: OrderStatus) {
        // given
        val order = OrderFixture.fixture(type = type, status = status)
        order.id = UUID.randomUUID()
        orderRepository.save(order)

        // when then
        assertThatThrownBy {
            orderService.complete(order.id)
        }
    }
}
