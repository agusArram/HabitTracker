package com.example.habittracker.presentation.dialog

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    isVacationActive: Boolean = false,
    vacationStartDate: String? = null,
    vacationEndDate: String? = null,
    onVacationToggle: (Boolean) -> Unit = {},
    onVacationDatesSelected: (String, String) -> Unit = { _, _ -> }
) {
    var isVacationMode by remember { mutableStateOf(isVacationActive) }
    var startDate by remember { mutableStateOf(vacationStartDate ?: "") }
    var endDate by remember { mutableStateOf(vacationEndDate ?: "") }
    
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val today = LocalDate.now()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val date = LocalDate.of(year, month + 1, day)
                onDateSelected(date.format(dateFormatter))
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        ).show()
    }
    
    fun formatForDisplay(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString, dateFormatter)
            date.format(displayFormatter)
        } catch (e: Exception) {
            dateString
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Configuración", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Vacation Mode Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🏖️ Modo Vacaciones",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mantiene las rachas durante el período",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isVacationMode,
                                onCheckedChange = { 
                                    isVacationMode = it
                                    onVacationToggle(it)
                                }
                            )
                        }
                        
                        if (isVacationMode) {
                            HorizontalDivider()
                            
                            // Start Date
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker { startDate = it } }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Fecha inicio",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = if (startDate.isNotEmpty()) formatForDisplay(startDate) else "Seleccionar",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Seleccionar fecha",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            // End Date
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker { endDate = it } }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Fecha fin",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = if (endDate.isNotEmpty()) formatForDisplay(endDate) else "Seleccionar",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Seleccionar fecha",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            Text(
                                text = "💡 Los hábitos marcados con 🏖️ mantendrán su racha durante las vacaciones",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isVacationMode && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                        onVacationDatesSelected(startDate, endDate)
                    }
                    onDismiss()
                }
            ) {
                Text("Guardar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

