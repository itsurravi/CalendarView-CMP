package com.ravikantsharma.cmp

import androidx.compose.ui.graphics.Color

// Extension to simulate fromHex if it's not available in CMP common
fun Color.Companion.fromHex(colorString: String): Color {
    return Color(longFromHex(colorString))
}

private fun longFromHex(colorString: String): Long {
    var data = colorString
    if (data.startsWith("#")) {
        data = data.substring(1)
    }
    val color = data.toLong(16)
    return if (data.length <= 6) {
        color or 0x00000000FF000000L
    } else {
        color
    }
}
