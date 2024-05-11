package domain

import kitchenpos.domain.MenuGroup
import java.util.UUID

object MenuGroupFixtures {
    fun makeMenuGroupOne(): MenuGroup {
        return MenuGroup().apply {
            this.id = UUID.randomUUID()
            this.name = "한마리 메뉴"
        }
    }
}
