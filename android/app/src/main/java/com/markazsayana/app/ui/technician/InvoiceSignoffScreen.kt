package com.markazsayana.app.ui.technician

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.markazsayana.app.ui.components.FullScreenError
import com.markazsayana.app.ui.components.FullScreenLoading
import com.markazsayana.app.ui.components.OutlinedCard
import com.markazsayana.app.ui.components.PrimaryButton
import com.markazsayana.app.ui.theme.BorderMedium
import com.markazsayana.app.ui.theme.CardShape
import com.markazsayana.app.ui.theme.CardWhite
import com.markazsayana.app.ui.theme.Divider
import com.markazsayana.app.ui.theme.PetrolGreen
import com.markazsayana.app.ui.theme.PetrolGreenChipBg
import com.markazsayana.app.ui.theme.PetrolGreenDark
import com.markazsayana.app.ui.theme.SuccessGreen
import com.markazsayana.app.ui.theme.SurfaceScreen
import com.markazsayana.app.ui.theme.TextPrimary
import com.markazsayana.app.ui.theme.TextSecondary
import com.markazsayana.app.ui.theme.TextTertiary
import com.markazsayana.app.ui.theme.UrgentRed
import com.markazsayana.app.util.egp

private val paymentMethods = listOf("card" to "شبكة", "cash" to "نقد", "account" to "على الحساب")

@Composable
fun InvoiceSignoffScreen(
    onBack: () -> Unit,
    onClosed: () -> Unit,
    viewModel: InvoiceSignoffViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.closed) {
        if (state.closed) onClosed()
    }

    Scaffold(containerColor = SurfaceScreen) { padding ->
        when {
            state.loading -> FullScreenLoading(Modifier.padding(padding))
            state.workOrder == null || state.invoice == null -> FullScreenError(state.error ?: "خطأ", onRetry = viewModel::load, modifier = Modifier.padding(padding))
            else -> {
                val wo = state.workOrder!!
                val invoice = state.invoice!!
                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
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
                            Text(text = "تسليم الزيارة", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(text = "${wo.code} · مراجعة الفاتورة", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        }
                    }

                    LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item {
                            OutlinedCard(shape = CardShape, padding = PaddingValues(16.dp)) {
                                InvoiceLine("أجرة الكشف والعمالة", invoice.laborFee.egp())
                                invoice.parts.forEach { InvoiceLine(it.name, it.price.egp()) }
                                if (invoice.warrantyDiscount > 0) {
                                    InvoiceLine("خصم الضمان", "−${invoice.warrantyDiscount.egp()}", valueColor = SuccessGreen)
                                }
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).height(1.dp).background(Divider))
                                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "الإجمالي مع الضريبة", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                    Text(text = invoice.total.egp(), style = com.markazsayana.app.ui.theme.Title22Bold, color = PetrolGreen)
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "طريقة الدفع", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    paymentMethods.forEach { (key, label) ->
                                        val selected = key == state.paymentMethod
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(CardShape)
                                                .background(if (selected) PetrolGreen else CardWhite)
                                                .border(1.dp, if (selected) PetrolGreen else BorderMedium, CardShape)
                                                .clickable { viewModel.selectPaymentMethod(key) }
                                                .padding(vertical = 13.dp),
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (selected) CardWhite else TextPrimary,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.align(Alignment.Center),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "توقيع العميل باستلام الجهاز", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                                SignaturePad()
                                OutlinedTextField(
                                    value = state.signatureName,
                                    onValueChange = viewModel::onSignatureNameChange,
                                    label = { Text("اسم العميل (توقيع)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PetrolGreen),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(PetrolGreenChipBg, CardShape).padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(modifier = Modifier.padding(top = 7.dp).size(8.dp).clip(CircleShape).background(PetrolGreen))
                                Text(
                                    text = "يُرسل تقرير الصيانة والفاتورة للعميل عبر واتساب بعد التسليم.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PetrolGreenDark,
                                )
                            }
                        }

                        state.error?.let { err ->
                            item { Text(text = err, color = UrgentRed, style = MaterialTheme.typography.bodySmall) }
                        }
                    }

                    Box(modifier = Modifier.background(CardWhite).padding(16.dp)) {
                        PrimaryButton(
                            text = if (state.submitting) "جارِ التأكيد…" else "تأكيد التسليم وإغلاق الأمر",
                            onClick = viewModel::confirm,
                            backgroundColor = SuccessGreen,
                            enabled = !state.submitting,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceLine(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor)
    }
}

@Composable
private fun SignaturePad() {
    var points by remember { mutableStateOf(listOf<Offset>()) }
    var hasDrawn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .background(CardWhite, CardShape)
            .border(1.dp, BorderMedium, CardShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { hasDrawn = true },
                    onDrag = { change, _ -> points = points + change.position },
                )
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!hasDrawn) {
                drawLine(
                    color = BorderMedium,
                    start = Offset(0f, size.height - 24f),
                    end = Offset(size.width, size.height - 24f),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                )
            }
            for (i in 1 until points.size) {
                drawLine(color = PetrolGreenDark, start = points[i - 1], end = points[i], strokeWidth = 4f)
            }
        }
        if (!hasDrawn) {
            Text(
                text = "وقّع هنا",
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
            )
        }
    }
}
