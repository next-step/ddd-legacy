package kitchenpos;

import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.OrderService;
import kitchenpos.application.OrderTableService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
public class IntegrationTest {
    @Autowired
    protected MenuGroupService menuGroupService;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected OrderTableService orderTableService;

    @Sql({"/truncate-all.sql"})
    @BeforeEach
    public void setUp() {

    }
}
