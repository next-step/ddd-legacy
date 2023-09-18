package kitchenpos.application;

import java.util.List;

import kitchenpos.domain.MenuProduct;

public class NullOrEmptyMenuProductRequestsException extends IllegalArgumentException {

    private static final String MESSAGE = "메뉴상품요청은 null이거나 비어있을 수 없습니다. 현재 값: [%s]";

    public NullOrEmptyMenuProductRequestsException(List<MenuProduct> menuProductsRequest) {
        super(String.format(MESSAGE, menuProductsRequest == null ? null : menuProductsRequest.size()));
    }
}
