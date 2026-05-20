package com.kad.power.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kad.power.presentation.viewmodel.SolarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: SolarViewModel,
    onNavigateBack: () -> Unit
) {
    val currentLoads by viewModel.currentLoads.collectAsState()
    val result by viewModel.calculationResult.collectAsState()

    var appName by remember { mutableStateOf("") }
    var watts by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var hours by remember { mutableStateOf("4") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حاسبة الأحمال الشمسية") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("حاسبة الأحمال الشمسية الهندسية", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("أدخل أحمال الأجهزة لتقدير حجم مصفوفة الألواح والبطاريات والإنفرتر الملائم تكنولوجياً.", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("إضافة جهاز نشط", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = appName,
                            onValueChange = { appName = it },
                            label = { Text("اسم الجهاز (مثلاً: غسالة، مكيف، غطاس)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = watts,
                                onValueChange = { watts = it },
                                label = { Text("القدرة (واط)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { quantity = it },
                                label = { Text("العدد") },
                                modifier = Modifier.weight(0.5f)
                            )
                            OutlinedTextField(
                                value = hours,
                                onValueChange = { hours = it },
                                label = { Text("ساعات التشغيل") },
                                modifier = Modifier.weight(0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            onClick = {
                                val w = watts.toDoubleOrNull() ?: 0.0
                                val q = quantity.toIntOrNull() ?: 1
                                val h = hours.toDoubleOrNull() ?: 1.0
                                if (appName.isNotEmpty() && w > 0) {
                                    viewModel.addLoadItem(appName, w, q, h)
                                    appName = ""
                                    watts = ""
                                }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("إضافة الحمل", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            if (currentLoads.isNotEmpty()) {
                item {
                    Text("الأجهزة المدخلة حاليًا", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                itemsIndexed(currentLoads) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.applianceName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${item.watts} واط × ${item.quantity} أجهزة × ${item.hoursPerDay} ساعات = ${item.watts * item.quantity * item.hoursPerDay} واط.ساعة/يومياً", fontSize = 11.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { viewModel.removeLoadItem(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف الحمل", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            result?.let { res ->
                if (res.totalDailyWattHours > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = Color(0xFFFFB300))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("التقديرات والنتائج الحصوية المقترحة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5D4037))
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                ResultRow(label = "إجمالي الاستهلاك اليومي:", value = "${res.totalDailyWattHours.toInt()} واط.ساعة/يوم")
                                ResultRow(label = "قدرة الألواح المطلوبة الكلية:", value = "${res.recommendedPvCapacityWatts.toInt()} واط")
                                ResultRow(label = "عدد الألواح المقترح (بفئة 450 واط):", value = "${res.estimatedPanelsCount} لوح مونو")
                                ResultRow(label = "العاكس المقترح (Inverter Capacity):", value = "${res.recommendedInverterWatts.toInt()} واط")
                                ResultRow(label = "سعة البطارية المطلوبة (نظام 24V):", value = "${res.batteryBankAh24v.toInt()} أمبير/ساعي")
                                ResultRow(label = "منظم الشحن MPPT المتوقع:", value = "${res.chargeControllerAmps.toInt()} أمبير")
                                ResultRow(label = "زاوية الميل التقريبية للألواح لشمس الشرق الأوسط:", value = "▲ ${String.format("%.1f", res.optimalTiltAngleDegrees)}° جنوباً")

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.saveCurrentCalculation() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("حفظ النتائج التقرير محلياً", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.DarkGray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
