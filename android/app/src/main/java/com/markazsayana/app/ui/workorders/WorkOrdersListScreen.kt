package com.markazsayana.app.ui.workorders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.markazsayana.app.data.remote.WorkOrderSummaryDto
import com.markazsayana.app.ui.components.BottomNavBar
import com.markazsayana.app.ui.components.FullScreenError
import com.markazsayana.app.ui.components.FullScreenLoading
import com.markazsayana.app.ui.components.OutlinedCard
import com.markazsayana.app.ui.components.StatusChip
import com.markazsayana.app.ui.components.navItemsForRole
import com.markazsayana.app.ui.navigation.Routes
import com.markazsayana.app.ui.theme.BorderMedium
import com.markazsayana.app.ui.theme.CardWhite
import com.markazsayana.app.ui.theme.OrangeChipBg
import com.markazsayana.app.ui.theme.OrangeTextDark
import com.markazsayana.app.ui.theme.PetrolGreen
import com.markazsayana.app.ui.theme.PetrolGreenChipBg
import com.markazsayana.app.ui.theme.SurfaceScreen
import com.markazsayana.app.ui.theme.TextPrimary
import com.markazsayana.app.ui.theme.TextTertiary
import com.markazsayana.app.util.dateLabelCairo
import java.time.Instant

private val nav = navItemsForRole("reception")

@Composable
fun WorkOrdersListScreen(
    onNavTab: (String) -> Unit,
    viewModel: WorkOrdersListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    state.selected?.let { wo ->
        WorkOrderDetailDialog(wo = wo, onDismiss = { viewModel.select(null) })
    }

    Scaffold(
        containerColor = SurfaceScreen,
        bottomBar = { BottomNavBar(nav, Routes.WORK_ORDERS_LIST, PetrolGreen, onNavTab) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().background(CardWhite).padding(16.dp)) {
                Text(text = "الطلبات", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                OutlinedTextField(
                    value = state.search,
                    onValueChange = viewModel::setSearch,
                    placeholder = { Text("بحث برقم الطلب أو اسم العميل…") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.runSearch() }),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PetrolGreen),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                    WorkOrderFilter.entries.forEach { filter ->
                        val selected = filter == state.filter
                        Box(
                            modifier = Modifier
                                .background(if (selected) PetrolGreen else CardWhite, RoundedCornerShape(9.dp))
                                .border(1.dp, if (selected) PetrolGreen else BorderMedium, RoundedCornerShape(9.dp))
                                .clickable { viewModel.setFilter(filter) }
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                        ) {
                            Text(text = filter.label, style = MaterialTheme.typography.bodySmall, color = if (selected) CardWhite else TextPrimary)
                        }
                    }
                }
            }

            when {
                state.loading -> FullScreenLoading()
                state.error != null -> FullScreenError(state.error!!, onRetry = viewModel::load)
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.items, key = { it.id }) { wo ->
                        WorkOrderRow(wo, onClick = { viewModel.select(wo) })
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkOrderRow(wo: WorkOrderSummaryDto, onClick: () -> Unit) {
    OutlinedCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${wo.deviceType} — ${wo.issueDescription.take(30)}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text(text = "${wo.code} · ${wo.customerName}", style = MaterialTheme.typography.bodySmall, color = TextTertiary, modifier = Modifier.padding(top = 3.dp))
            }
            if (wo.technicianName != null) {
                StatusChip(text = wo.statusLabel, background = PetrolGreenChipBg, contentColor = PetrolGreen)
            } else {
                StatusChip(text = wo.statusLabel, background = OrangeChipBg, contentColor = OrangeTextDark)
            }
        }
    }
}

@Composable
private fun WorkOrderDetailDialog(wo: WorkOrderSummaryDto, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(wo.code) },
        text = {
            Column {
                DetailLine("الحالة", wo.statusLabel)
                DetailLine("الجهاز", wo.deviceType)
                DetailLine("العميل", wo.customerName)
                DetailLine("العطل", wo.issueDescription)
                DetailLine("الفرع", wo.branch ?: "—")
                DetailLine("الفني", wo.technicianName ?: "بلا فني")
                DetailLine("تاريخ الإنشاء", runCatching { Instant.parse(wo.createdAt).dateLabelCairo() }.getOrDefault(wo.createdAt))
                wo.closedAt?.let { DetailLine("تاريخ الإغلاق", runCatching { Instant.parse(it).dateLabelCairo() }.getOrDefault(it)) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق", color = PetrolGreen) }
        },
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}
