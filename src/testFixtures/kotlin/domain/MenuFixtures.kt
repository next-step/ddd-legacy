package domain

import domain.MenuGroupFixtures.makeMenuGroupOne
import domain.MenuProductFixtures.makeMenuProductOne
import kitchenpos.domain.Menu
import java.math.BigDecimal
import java.util.UUID

object MenuFixtures {
    fun makeMenuOne(): Menu {
        return Menu().apply {
            this.id = UUID.randomUUID()
            this.name = "후라이드 치킨"
            this.price = BigDecimal.valueOf(16000)
            this.menuGroup = makeMenuGroupOne()
            this.isDisplayed = true
            this.menuProducts = listOf(makeMenuProductOne())
        }
    }
}
