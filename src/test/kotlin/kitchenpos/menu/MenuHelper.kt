package kitchenpos.menu

import io.restassured.RestAssured
import kitchenpos.domain.Menu

class MenuHelper {
    companion object {
        fun 메뉴_이름으로_메뉴_조회(name: String): Menu {
            return RestAssured
                .get("/api/menus")
                .then().log().all().extract().`as`(Array<Menu>::class.java)
                .find { it.name == name }!!
        }
    }
}
