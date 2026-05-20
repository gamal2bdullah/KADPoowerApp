package com.kad.power.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.WaterDrop
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
fun HomeScreen(
    viewModel: SolarViewModel,
    onNavigateToCalculator: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToContact: () -> Unit
) {
    val isOnline by viewModel.isOnline.collectAsState()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()
    val syncInProgress by viewModel.syncInProgress.collectAsState()

    val amberPrimary = Color(0xFFFFB300)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "KAD للطاقة البديلة", 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    ) 
                },
                actions = {
                    if (!isOnline) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("دون اتصال", fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    } else if (pendingSyncCount > 0) {
                        Surface(
                            color = amberPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { viewModel.triggerBackgroundSync() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (syncInProgress) "مزامنة..." else "مزامنة معلقة ($pendingSyncCount)", 
                                    fontSize = 11.sp, 
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "الخلية الضوئية للطاقة البديلة",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "أنظمتك المتكاملة للطاقة الشمسية والضخ الكهروضوئي للزراعة بأعلى جودة ومقاومة للاعتمادية.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = amberPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        onClick = onNavigateToContact
                    ) {
                        Text("طلب استشارة ودراسة مجانية", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("الوصول السريع للأدوات الشروحات", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    onClick = onNavigateToCalculator,
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .padding(end = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = amberPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("حاسبة الأحمال", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("صمم نظامك الكهروضوئي", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Card(
                    onClick = onNavigateToCatalog,
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .padding(start = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF03A9F4))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("كتالوج المنتجات", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("تصفح الألواح ومقومات الضخ", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.0f)) {
                        Text("أنظمة الضخ الشمسي للزراعة", color = Color(0xFF006064), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("تصميم كفؤ لتشغيل غطاسات المياه والمضخات الزراعية العميقة بالطاقة الشمسية المباشرة ودون بطاريات مكلفة.", fontSize = 12.sp, color = Color(0xFF006064).copy(alpha = 0.8f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.WaterDrop, 
                        contentDescription = null, 
                        tint = Color(0xFF00ACC1),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
