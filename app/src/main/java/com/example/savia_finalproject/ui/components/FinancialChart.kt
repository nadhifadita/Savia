package com.example.savia_finalproject.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

// Warna Tema
private val BluePrimary = Color(0xFF0052D4)
private val YellowAccent = Color(0xFFFFC107)

@Composable
fun FinancialChart(
    weeklyEntries: List<Entry>,
    weeklyLabels: List<String>,
    monthlyEntries: List<Entry>,
    monthlyLabels: List<String>
) {
    var isWeekly by remember { mutableStateOf(true) }

    val currentEntries = if (isWeekly) weeklyEntries else monthlyEntries
    val currentLabels = if (isWeekly) weeklyLabels else monthlyLabels
    val title = if (isWeekly) "Perangkaan" else "Perangkaan"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // HEADER: Judul & Toggle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Ringkasan saldo",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // TOGGLE BUTTON (Mingguan | Bulanan)
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5F7FA), RoundedCornerShape(20.dp))
                        .padding(4.dp)
                ) {
                    ChartFilterChip("Mingguan", isSelected = isWeekly) { isWeekly = true }
                    Spacer(modifier = Modifier.width(4.dp))
                    ChartFilterChip("Bulanan", isSelected = !isWeekly) { isWeekly = false }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CHART AREA
            if (currentEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data transaksi", color = Color.LightGray)
                }
            } else {
                // Gunakan Crossfade untuk animasi halus saat ganti mode
                Crossfade(targetState = currentEntries, label = "ChartAnimation") { entries ->
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        factory = { context ->
                            LineChart(context).apply {
                                description.isEnabled = false
                                legend.isEnabled = false
                                isDragEnabled = true
                                setScaleEnabled(false)
                                setPinchZoom(false)
                                setDrawBorders(false)
                                axisRight.isEnabled = false

                                xAxis.apply {
                                    position = XAxis.XAxisPosition.BOTTOM
                                    setDrawGridLines(false)
                                    setDrawAxisLine(false)
                                    granularity = 1f
                                    textColor = AndroidColor.parseColor("#999999")
                                    textSize = 10f
                                    yOffset = 10f
                                }

                                axisLeft.apply {
                                    setDrawGridLines(true)
                                    gridColor = AndroidColor.parseColor("#F0F0F0")
                                    textColor = AndroidColor.parseColor("#999999")
                                    setDrawAxisLine(false)
                                    setDrawZeroLine(false)
                                    textSize = 10f
                                }
                            }
                        },
                        update = { chart ->
                            // Update Formatter Sumbu X
                            chart.xAxis.valueFormatter = IndexAxisValueFormatter(currentLabels)

                            val dataSet = LineDataSet(entries, "Saldo").apply {
                                color = BluePrimary.toArgb()
                                lineWidth = 3f
                                setCircleColor(YellowAccent.toArgb())
                                circleRadius = 5f
                                setDrawCircleHole(true)
                                circleHoleColor = AndroidColor.WHITE
                                valueTextColor = AndroidColor.BLACK
                                setDrawValues(false)
                                mode = LineDataSet.Mode.CUBIC_BEZIER
                                setDrawFilled(true)
                                fillColor = YellowAccent.toArgb()
                                fillAlpha = 60
                            }

                            chart.data = LineData(dataSet)
                            chart.notifyDataSetChanged() // Penting agar chart sadar data berubah
                            chart.invalidate()
                            chart.animateY(800) // Animasi ulang saat ganti
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChartFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .then(if (isSelected) Modifier.padding(1.dp) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BluePrimary else Color.Gray
        )
    }
}