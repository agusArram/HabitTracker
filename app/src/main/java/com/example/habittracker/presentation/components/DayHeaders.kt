package com.example.habittracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.R
import java.time.LocalDate

@Composable
fun DayHeaders(daysInWeek: List<LocalDate>) {
    val dayNames = listOf(
        stringResource(R.string.day_monday_short),
        stringResource(R.string.day_tuesday_short),
        stringResource(R.string.day_wednesday_short),
        stringResource(R.string.day_thursday_short),
        stringResource(R.string.day_friday_short),
        stringResource(R.string.day_saturday_short),
        stringResource(R.string.day_sunday_short)
    )
    
    val today = LocalDate.now()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(120.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.header_habit),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dayNames.forEachIndexed { index, dayName ->
                val currentDay = if (index < daysInWeek.size) daysInWeek[index] else null
                val isToday = currentDay == today
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isToday) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    if (currentDay != null) {
                        Box(
                            modifier = Modifier
                                .size(if (isToday) 28.dp else 24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isToday) 
                                        MaterialTheme.colorScheme.primaryContainer
                                    else 
                                        androidx.compose.ui.graphics.Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentDay.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = if (isToday) 12.sp else 9.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
