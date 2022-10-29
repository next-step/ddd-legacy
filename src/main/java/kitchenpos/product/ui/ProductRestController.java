package kitchenpos.product.ui;

import kitchenpos.product.application.ProductCrudService;
import kitchenpos.product.domain.Product;
import kitchenpos.product.dto.request.ProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/products")
@RestController
public class ProductRestController {
    private final ProductCrudService productCrudService;

    public ProductRestController(final ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody final ProductRequest request) {
        final Product response = productCrudService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{productId}/price")
    public ResponseEntity<Product> changePrice(@PathVariable final UUID productId, @RequestBody final Product request) {
        return ResponseEntity.ok(productCrudService.changePrice(productId, request));
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productCrudService.findAll());
    }
}
