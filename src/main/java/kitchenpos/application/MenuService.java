package kitchenpos.application;

import kitchenpos.application.Exception.*;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import static kitchenpos.application.Exception.ErrorCode.*;

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
    public Menu create(final Menu request) throws MenuException {
        final BigDecimal price = request.getPrice();
        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new MenuException(MENU_PRICE_INVALID);
        }
        final MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId())
            .orElseThrow(() -> new MenuException(MENU_GROUP_NOT_FOUND));

        final List<MenuProduct> menuProductRequests = request.getMenuProducts();
        if (Objects.isNull(menuProductRequests) || menuProductRequests.isEmpty()) {
            throw new MenuException(MENU_PRODUCTS_EMPTY);
        }

        final List<Product> products = productRepository.findAllByIdIn(
            menuProductRequests.stream()
                .map(MenuProduct::getProductId)
                .toList()
        );
        if (products.size() != menuProductRequests.size()) {
            throw new MenuException(MENU_PRODUCTS_SIZE_NOT_MATCHED);
        }
        final List<MenuProduct> menuProducts = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProductRequest : menuProductRequests) {
            final long quantity = menuProductRequest.getQuantity();
            if (quantity < 0) {
                throw new MenuException(MENU_QUANTITY_NEGATIVE);
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
            throw new MenuException(MENU_PRICE_INVALID);
        }
        final String name = request.getName();
        if (Objects.isNull(name) || purgomalumClient.containsProfanity(name)) {
            throw new MenuException(MENU_NAME_INVALID);
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
            throw new MenuException(MENU_PRICE_INVALID);
        }
        BigDecimal sum = BigDecimal.ZERO;
        final Menu menu = menuRepository.findById(menuId)
            .orElseThrow(()-> new MenuNotFoundException(MENU_NOT_FOUND));
        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            sum = sum.add(
                menuProduct.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(menuProduct.getQuantity()))
            );
        }
        if (price.compareTo(sum) > 0) {
            throw new MenuException(MENU_PRICE_INVALID);
        }
        menu.setPrice(price);
        return menu;
    }

    @Transactional
    public Menu display(final UUID menuId) {
        final Menu menu = menuRepository.findById(menuId)
            .orElseThrow(()->
                new MenuNotFoundException(MENU_NOT_FOUND)
            );
        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            sum = sum.add(
                menuProduct.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(menuProduct.getQuantity()))
            );
        }
        if (menu.getPrice().compareTo(sum) > 0) {
            throw new MenuException(MENU_PRICE_INVALID);
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
