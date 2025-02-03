package kitchenpos.application

import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderType
import kitchenpos.infra.KitchenridersClient
import kitchenpos.order.OrderFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
}
