package kitchenpos.application;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Name;
import kitchenpos.domain.ProductRepository;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.domain.Price;
import kitchenpos.menu.menugroup.infra.PurgomalumClient;
import kitchenpos.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final PurgomalumClient purgomalumClient;

    public ProductService(
            final ProductRepository productRepository,
            final MenuRepository menuRepository,
            final PurgomalumClient purgomalumClient
    ) {
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
        this.purgomalumClient = purgomalumClient;
    }

    @Transactional
    public Product create(final Product request) {
        final String name = request.getName();
        final Product product = new Product(new Name(name, purgomalumClient.containsProfanity(name)), new Price(request.getPrice()));
        product.setId(UUID.randomUUID());
        product.setName(name);
        return productRepository.save(product);
    }

    @Transactional
    public Product changePrice(final UUID productId, final Product request) {
        final BigDecimal price = request.getPrice();
        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        final Product product = productRepository.findById(productId)
                .orElseThrow(NoSuchElementException::new);
        //TODO price Type변경
//        product.setPrice(price);
        final List<Menu> menus = menuRepository.findAllByProductId(productId);
        for (final Menu menu : menus) {
            BigDecimal sum = BigDecimal.ZERO;
            for (final MenuProduct menuProduct : menu.getMenuProducts()) {
                sum = menuProduct.getProduct()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(menuProduct.getQuantity().getQuantity()));
            }
            if (menu.getPrice().compareTo(sum) > 0) {
                menu.setDisplayed(false);
            }
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
