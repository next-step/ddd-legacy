package kitchenpos.application;

import kitchenpos.domain.MenuProduct;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.domain.Price;
import kitchenpos.menu.menu.domain.Quantity;
import kitchenpos.menu.menu.dto.MenuProductRequest;
import kitchenpos.menu.menu.dto.MenuRequest;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import kitchenpos.menu.menugroup.infra.PurgomalumClient;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;
    private final PurgomalumClient purgomalumClient;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuGroupRepository menuGroupRepository,
            final ProductRepository productRepository,
            final PurgomalumClient purgomalumClient
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
        this.purgomalumClient = purgomalumClient;
    }

    @Transactional
    public Menu create(final MenuRequest request) {
        final BigDecimal price = request.getPrice();
        final MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId())
                .orElseThrow(NoSuchElementException::new);
        final List<MenuProductRequest> menuProductRequests = request.getMenuProducts();
        if (Objects.isNull(menuProductRequests) || menuProductRequests.isEmpty()) {
            throw new IllegalArgumentException();
        }
        final List<Product> products = productRepository.findAllByIdIn(
                menuProductRequests.stream()
                        .map(MenuProductRequest::getProductId)
                        .collect(Collectors.toList())
        );
        validateProductSize(menuProductRequests, products);
        final List<MenuProduct> menuProducts = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProductRequest menuProductRequest : menuProductRequests) {
            Quantity quantity = new Quantity(menuProductRequest.getQuantity());
            final Product product = productRepository.findById(menuProductRequest.getProductId())
                    .orElseThrow(NoSuchElementException::new);
            sum = sum.add(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(quantity.getQuantity())));
            final MenuProduct menuProduct = new MenuProduct(product, quantity);
            menuProduct.setProduct(product);
            menuProducts.add(menuProduct);
        }
        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException();
        }
        final String name = request.getName();
        if (Objects.isNull(name) || purgomalumClient.containsProfanity(name)) {
            throw new IllegalArgumentException();
        }
        final Menu menu = new Menu(menuGroup, menuProducts, new Price(price));
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setDisplayed(request.isDisplayed());
        menu.setMenuProducts(menuProducts);
        return menuRepository.save(menu);
    }

    private static void validateProductSize(List<MenuProductRequest> menuProductRequests, List<Product> products) {
        if (products.size() != menuProductRequests.size()) {
            throw new IllegalArgumentException("상품의 수량과 메뉴 상품의 수량은 다를 수 없다.");
        }
    }

    @Transactional
    public Menu changePrice(final UUID menuId, final MenuRequest request) {
        final BigDecimal price = request.getPrice();
        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            final BigDecimal sum = menuProduct.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(menuProduct.getQuantity().getQuantity()));
            if (price.compareTo(sum) > 0) {
                throw new IllegalArgumentException();
            }
        }
        //TODO price 타입 변경
//        menu.setPrice(price);
        return menu;
    }

    @Transactional
    public Menu display(final UUID menuId) {
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            final BigDecimal sum = menuProduct.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(menuProduct.getQuantity().getQuantity()));
            if (menu.getPrice().compareTo(sum) > 0) {
                throw new IllegalStateException();
            }
        }
        menu.setDisplayed(true);
        return menu;
    }

    @Transactional
    public Menu hide(final UUID menuId) {
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        menu.setDisplayed(false);
        return menu;
    }

    @Transactional(readOnly = true)
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }
}
