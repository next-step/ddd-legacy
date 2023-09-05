package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.infra.PurgomalumClient;
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
    private final ProfanityClient profanityClient;

    public MenuService(
        final MenuRepository menuRepository,
        final MenuGroupRepository menuGroupRepository,
        final ProductRepository productRepository,
        final ProfanityClient profanityClient
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
        this.profanityClient = profanityClient;
    }

    @Transactional
    public Menu create(final Menu request) {
        final BigDecimal price = request.getPrice();
        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        final MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId())
            .orElseThrow(NoSuchElementException::new);
        final List<MenuProduct> menuProductRequests = request.getMenuProducts();
        if (Objects.isNull(menuProductRequests) || menuProductRequests.isEmpty()) {
            throw new IllegalArgumentException();
        }
        final List<Product> products = productRepository.findAllByIdIn(
            menuProductRequests.stream()
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList())
        );
        if (products.size() != menuProductRequests.size()) {
            throw new IllegalArgumentException();
        }
        final List<MenuProduct> menuProducts = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProductRequest : menuProductRequests) {
            final long quantity = menuProductRequest.getQuantity();
            if (quantity < 0) {
                throw new IllegalArgumentException();
            }
            final Product product = productRepository.findById(menuProductRequest.getProductId())
                .orElseThrow(NoSuchElementException::new);
            sum = sum.add(
                product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity))
            );
            final MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(quantity);
            menuProducts.add(menuProduct);
        }
        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException();
        }
        final String name = request.getName();
        if (Objects.isNull(name) || profanityClient.containsProfanity(name)) {
            throw new IllegalArgumentException();
        }
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(request.isDisplayed());
        menu.setMenuProducts(menuProducts);
        return menuRepository.save(menu);
    }

    @Transactional
    public Menu changePrice(final UUID menuId, final Menu request) {
        final BigDecimal price = request.getPrice();
        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        final Menu menu = menuRepository.findById(menuId)
            .orElseThrow(NoSuchElementException::new);
        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            final BigDecimal sum = menuProduct.getProduct()
                .getPrice()
                .multiply(BigDecimal.valueOf(menuProduct.getQuantity()));
            if (price.compareTo(sum) > 0) {
                throw new IllegalArgumentException();
            }
        }
        menu.setPrice(price);
        return menu;
    }

    @Transactional
    public Menu display(final UUID menuId) {
        final Menu menu = menuRepository.findById(menuId)
            .orElseThrow(NoSuchElementException::new);
        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            final BigDecimal sum = menuProduct.getProduct()
                .getPrice()
                .multiply(BigDecimal.valueOf(menuProduct.getQuantity()));
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
