package domain

import kitchenpos.domain.MenuGroup
import java.util.UUID

object MenuGroupFixtures {
    fun makeMenuGroupOne(): MenuGroup {
        return MenuGroup().apply {
            this.id = UUID.fromString("df8d7e4f-4283-4c91-9e43-d9e2dcb6f182")
            this.name = "한마리 메뉴"
        }
    }
}
