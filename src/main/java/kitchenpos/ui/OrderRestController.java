package kitchenpos.ui;

import kitchenpos.order.application.OrderCrudService;
import kitchenpos.order.application.OrderStatusService;
import kitchenpos.order.domain.Order;
import kitchenpos.order.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/orders")
@RestController
public class OrderRestController {
    private final OrderCrudService orderCrudService;
    private final OrderStatusService orderStatusService;

    public OrderRestController(final OrderCrudService orderCrudService, final OrderStatusService orderStatusService) {
        this.orderCrudService = orderCrudService;
        this.orderStatusService = orderStatusService;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody final OrderRequest request) {
        final Order response = orderCrudService.create(request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{orderId}/accept")
    public ResponseEntity<Order> accept(@PathVariable final UUID orderId) {
        return ResponseEntity.ok(orderStatusService.accept(orderId));
    }

    @PutMapping("/{orderId}/serve")
    public ResponseEntity<Order> serve(@PathVariable final UUID orderId) {
        return ResponseEntity.ok(orderStatusService.serve(orderId));
    }

    @PutMapping("/{orderId}/start-delivery")
    public ResponseEntity<Order> startDelivery(@PathVariable final UUID orderId) {
        return ResponseEntity.ok(orderStatusService.startDelivery(orderId));
    }

    @PutMapping("/{orderId}/complete-delivery")
    public ResponseEntity<Order> completeDelivery(@PathVariable final UUID orderId) {
        return ResponseEntity.ok(orderStatusService.completeDelivery(orderId));
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<Order> complete(@PathVariable final UUID orderId) {
        return ResponseEntity.ok(orderStatusService.complete(orderId));
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderCrudService.findAll());
    }
}
