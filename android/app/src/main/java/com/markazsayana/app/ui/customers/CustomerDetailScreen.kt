package com.markazsayana.app.ui.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.markazsayana.app.data.remote.CustomerDetailResponse
import com.markazsayana.app.data.remote.CustomerWorkOrderDto
import com.markazsayana.app.data.remote.DeviceDto
import com.markazsayana.app.ui.components.FullScreenError
import com.markazsayana.app.ui.components.FullScreenLoading
import com.markazsayana.app.ui.components.OutlinedCard
import com.markazsayana.app.ui.components.StatusChip
import com.markazsayana.app.ui.theme.CardWhite
import com.markazsayana.app.ui.theme.OrangeChipBg
import com.markazsayana.app.ui.theme.OrangeTextDark
import com.markazsayana.app.ui.theme.PetrolGreen
import com.markazsayana.app.ui.theme.PetrolGreenChipBg
import com.markazsayana.app.ui.theme.SurfaceScreen
import com.markazsayana.app.ui.theme.TextPrimary
import com.markazsayana.app.ui.theme.TextTertiary

@Composable
fun CustomerDetailScreen(
    onBack: () -> Unit,
    viewModel: CustomerDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(containerColor = SurfaceScreen) { padding ->
        when (val s = state) {
            is CustomerDetailUiState.Loading -> FullScreenLoading(Modifier.padding(padding))
            is CustomerDetailUiState.Error -> FullScreenError(s.message, onRetry = viewModel::load, modifier = Modifier.padding(padding))
            is CustomerDetailUiState.Success -> Content(s.data, onBack, Modifier.padding(padding))
        }
    }
}

@Composable
private fun Content(data: CustomerDetailResponse, onBack: () -> Unit, modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(CardWhite).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceScreen).clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) { Text(text = "→", style = MaterialTheme.typography.titleMedium, color = TextPrimary) }
            Column {
                Text(text = data.customer.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = data.customer.phone, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            }
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (data.customer.address != null) {
                item {
                    OutlinedCard {
                        Text(text = "العنوان", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text(text = data.customer.address, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.padding(top = 3.dp))
                    }
                }
            }

            item { Text(text = "الأجهزة", style = MaterialTheme.typography.titleSmall, color = TextPrimary) }
            items(data.devices, key = { it.id }) { d -> DeviceRow(d) }

            item { Text(text = "سجل الطلبات", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(top = 6.dp)) }
            items(data.workOrders, key = { it.id }) { wo -> WorkOrderHistoryRow(wo) }
        }
    }
}

@Composable
private fun DeviceRow(device: DeviceDto) {
    OutlinedCard {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = device.deviceType, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text(
                    text = listOfNotNull(device.brand, device.model).joinToString(" · ").ifBlank { "—" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            if (device.underWarranty) {
                StatusChip(text = "داخل الضمان", background = PetrolGreenChipBg, contentColor = PetrolGreen)
            }
        }
    }
}

@Composable
private fun WorkOrderHistoryRow(wo: CustomerWorkOrderDto) {
    OutlinedCard {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${wo.deviceType} — ${wo.issueDescription.take(28)}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text(text = "${wo.code} · ${wo.technicianName ?: "بلا فني"}", style = MaterialTheme.typography.bodySmall, color = TextTertiary, modifier = Modifier.padding(top = 3.dp))
            }
            if (wo.status == "closed") {
                StatusChip(text = "مغلقة", background = PetrolGreenChipBg, contentColor = PetrolGreen)
            } else {
                StatusChip(text = "مفتوحة", background = OrangeChipBg, contentColor = OrangeTextDark)
            }
        }
    }
}
