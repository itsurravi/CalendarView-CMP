package com.ravikantsharma.cmp.caledarview

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

// Helper to get display name of DayOfWeek (simplified for CMP)
fun DayOfWeek.getDisplayName(): String {
    return when (this) {
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
        DayOfWeek.SUNDAY -> "Sun"
    }
}

// Helper to get display name of Month
fun Month.getDisplayName(): String {
    return this.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
}

fun Month.getFullDisplayName(): String {
    return this.name.lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    dayHeaderContent: @Composable (DayOfWeek) -> Unit = { dayOfWeek ->
        Text(
            text = dayOfWeek.getDisplayName(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        )
    },
    dateContent: @Composable (LocalDate) -> Unit = { date ->
        Text(
            text = date.day.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    },
    onDateSelected: (LocalDate) -> Unit
) {
    var isMonthView by remember { mutableStateOf(true) }
    var referenceDate by remember { 
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }

    Column(modifier = modifier.fillMaxWidth().animateContentSize()) {

        CalendarHeader(
            currentDate = referenceDate,
            isMonthView = isMonthView,
            onPrevious = {
                referenceDate = if (isMonthView) {
                    referenceDate.minus(1, DateTimeUnit.MONTH)
                } else {
                    referenceDate.minus(7, DateTimeUnit.DAY)
                }
            },
            onNext = {
                referenceDate = if (isMonthView) {
                    referenceDate.plus(1, DateTimeUnit.MONTH)
                } else {
                    referenceDate.plus(7, DateTimeUnit.DAY)
                }
            },
            onToggleView = { isMonthView = !isMonthView }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                Box(modifier = Modifier.weight(1f)) { dayHeaderContent(dayOfWeek) }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = isMonthView,
            transitionSpec = {
                val duration = 300
                if (targetState) {
                    (expandVertically(animationSpec = tween(duration)) + fadeIn(animationSpec = tween(duration)))
                        .togetherWith(fadeOut(animationSpec = tween(duration)))
                        .using(SizeTransform(clip = false))
                } else {
                    (fadeIn())
                        .togetherWith(shrinkVertically(animationSpec = tween(duration)) + fadeOut(animationSpec = tween(duration)))
                        .using(SizeTransform(clip = false))
                }
            },
            contentAlignment = Alignment.TopCenter,
            label = "CalendarViewAnimation"
        ) { showMonth ->
            if (showMonth) {
                val year = referenceDate.year
                val month = referenceDate.month
                val firstOfMonth = LocalDate(year, month, 1)
                val daysInMonth = month.length(isLeapYear(year))
                val startOffset = firstOfMonth.dayOfWeek.isoDayNumber - 1

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.heightIn(min = 300.dp),
                    userScrollEnabled = false
                ) {
                    items(startOffset) { Box(modifier = Modifier.size(40.dp)) }
                    items(daysInMonth) { index ->
                        val date = LocalDate(year, month, index + 1)
                        Box(
                            modifier = Modifier.clickable(
                                onClick = { onDateSelected(date) },
                                interactionSource = null,
                                indication = null
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            dateContent(date)
                        }
                    }
                }
            } else {
                val startOfWeek = referenceDate.minus(referenceDate.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)

                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    for (i in 0 until 7) {
                        val date = startOfWeek.plus(i, DateTimeUnit.DAY)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            dateContent(date)
                        }
                    }
                }
            }
        }
    }
}

fun Month.length(isLeapYear: Boolean): Int {
    return when (this) {
        Month.FEBRUARY -> if (isLeapYear) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
}

fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

@Composable
fun CalendarHeader(
    currentDate: LocalDate,
    isMonthView: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleView: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }

            val title = if (isMonthView) {
                "${currentDate.month.getFullDisplayName()} ${currentDate.year}"
            } else {
                val startOfWeek = currentDate.minus(currentDate.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)
                val startMonth = startOfWeek.month.getDisplayName()
                val endMonth = endOfWeek.month.getDisplayName()
                if (startMonth == endMonth) {
                    "$startMonth ${startOfWeek.day}-${endOfWeek.day}"
                } else {
                    "$startMonth ${startOfWeek.day} - $endMonth ${endOfWeek.day}"
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }

        IconButton(onClick = onToggleView) {
            Icon(
                imageVector = if (isMonthView) Icons.Outlined.DateRange else Icons.Filled.DateRange,
                contentDescription = "Toggle View"
            )
        }
    }
}
