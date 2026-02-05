package com.ravikantsharma.cmp.caledarview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cmp.composeapp.generated.resources.Res
import cmp.composeapp.generated.resources.ic_live_class_calendar_grey_day
import cmp.composeapp.generated.resources.ic_live_class_calendar_grey_tick
import cmp.composeapp.generated.resources.ic_live_class_calendar_selected_day
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

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

@Composable
fun MyCustomCalendarScreen(
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Text("Select a Date:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        CalendarView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

            // --- Custom Day Header (Mon, Tue...) ---
            dayHeaderContent = { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName().uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },

            // --- Custom Date Cell (1, 2, 3...) ---
            dateContent = { date ->
                val isSelected = date == selectedDate
                val isToday = date == today

                // Define colors based on state
                val iconRes = when {
                    isSelected -> Res.drawable.ic_live_class_calendar_selected_day
                    isToday -> Res.drawable.ic_live_class_calendar_grey_tick
                    else -> Res.drawable.ic_live_class_calendar_grey_day
                }

                val batchStart = LocalDate(2026, 1, 4)
                val batchEnd = LocalDate(2026, 2, 20)

                // 3. Base visibility rules
                var showLeft = date > batchStart && date <= today && date.day > 1
                var showRight = date in batchStart..<today && date.day < date.month.length(isLeapYear(date.year))

                // 4. Batch-end rule -> no right side
                if (date == batchEnd) {
                    showRight = false
                }

                // 5. Week boundary rules
                if (date.dayOfWeek == DayOfWeek.MONDAY) {
                    showLeft = false
                }

                if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                    showRight = false
                }

                // Render the cell
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.weight(1f), // Make it square
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box(modifier = Modifier.weight(0.5f)) {
                                    if (showLeft) {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                                .clip(RoundedCornerShape(0.dp))
                                                .background(Color.fromHex("#FFF7E5"))
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(0.5f)) {
                                    if (showRight) {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                                .clip(RoundedCornerShape(0.dp))
                                                .background(Color.fromHex("#FFF7E5"))
                                        )
                                    }
                                }
                            }
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = date.day.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },

            // --- Action ---
            onDateSelected = { date ->
                selectedDate = date
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text("Selected: ${selectedDate ?: "None"}")
    }
}

@Preview
@Composable
private fun CalenderScreenPreview() {
    Scaffold {
        MaterialTheme {
            MyCustomCalendarScreen()
        }
    }
}
