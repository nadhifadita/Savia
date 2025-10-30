package com.example.savia_finalproject.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

@Composable
fun WeeklyChart(
    entries: List<Entry>,
    labels: List<String>
) {
    if (entries.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(
                "Mulai tambahkan transaksi untuk melihat grafik mingguan.",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        }
        return
    }

    Column {
        Text(text = "Ringkasan Mingguan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Gunakan AndroidView untuk membungkus LineChart dari MPAndroidChart
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { context ->
                LineChart(context).apply {
                    // Pengaturan dasar
                    description.isEnabled = false
                    legend.isEnabled = false
                    isDragEnabled = false
                    setScaleEnabled(false)

                    // Pengaturan Sumbu X (bawah)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.textColor = AndroidColor.GRAY
                    xAxis.axisLineColor = AndroidColor.LTGRAY
                    xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                    // Pengaturan Sumbu Y (kiri)
                    axisLeft.setDrawGridLines(true)
                    axisLeft.textColor = AndroidColor.GRAY
                    axisLeft.axisLineColor = AndroidColor.TRANSPARENT
                    axisLeft.setDrawZeroLine(true)
                    axisLeft.zeroLineColor = AndroidColor.GRAY

                    // Pengaturan Sumbu Y (kanan)
                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                // Buat set data
                val dataSet = LineDataSet(entries, "Saldo Harian").apply {
                    color = Color(0xFF247CF0).toArgb()
                    valueTextColor = AndroidColor.BLACK
                    setCircleColor(Color(0xFF247CF0).toArgb())
                    circleRadius = 4f
                    setDrawCircleHole(false)
                    lineWidth = 2.5f
                    setDrawValues(false) // Sembunyikan nilai di atas titik
                    mode = LineDataSet.Mode.CUBIC_BEZIER // Membuat garis melengkung
                    setDrawFilled(true) // Mengaktifkan area di bawah garis
                    fillColor = Color(0xFF247CF0).toArgb()
                    fillAlpha = 50 // Transparansi area
                }

                // Masukkan data ke chart dan refresh
                chart.data = LineData(dataSet)
                chart.invalidate() // Refresh chart
            }
        )
    }
}
