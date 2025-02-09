package random

import java.util.*

fun randomPrice() = Random().nextLong(0, Long.MAX_VALUE).toBigDecimal()
