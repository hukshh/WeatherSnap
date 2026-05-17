package com.weathersnap.ui.createreport

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateBack: () -> Unit,
    onReportSaved: () -> Unit,
    navController: androidx.navigation.NavController,
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val imagePath by viewModel.imagePath.collectAsState()
    val originalSize by viewModel.originalSize.collectAsState()
    val compressedSize by viewModel.compressedSize.collectAsState()
    val weather = viewModel.weather

    // Observe result from CameraScreen using the current backstack entry's state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    LaunchedEffect(navBackStackEntry) {
        val savedStateHandle = navBackStackEntry?.savedStateHandle ?: return@LaunchedEffect
        
        // Use getStateFlow to observe changes from the CameraScreen
        val imgPathFlow = savedStateHandle.getStateFlow<String?>("imagePath", null)
        val origPathFlow = savedStateHandle.getStateFlow<String?>("originalPath", null)
        val origSizeFlow = savedStateHandle.getStateFlow("originalSize", 0L)
        val compSizeFlow = savedStateHandle.getStateFlow("compressedSize", 0L)
        
        // When we get a new image path, update the ViewModel
        if (imgPathFlow.value != null && origPathFlow.value != null) {
            viewModel.onImageCaptured(
                origPath = origPathFlow.value!!,
                compPath = imgPathFlow.value!!,
                original = origSizeFlow.value,
                compressed = compSizeFlow.value
            )
            // Clear the results after consuming them in the ViewModel
            savedStateHandle["imagePath"] = null
            savedStateHandle["originalPath"] = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GrayTop, GrayBottom)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    title = { Text("Create Snap Report", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                
                WeatherSnapshotCard(weather)

                Spacer(modifier = Modifier.height(24.dp))

                ImagePreviewArea(
                    imagePath = imagePath,
                    originalSize = originalSize,
                    compressedSize = compressedSize,
                    onCaptureClick = onNavigateToCamera
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = viewModel::onNotesChange,
                    placeholder = { Text("Add some notes about this snap...", color = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.saveReport(onReportSaved) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = imagePath != null,
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = GrayBottom,
                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Save Snap Report", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun WeatherSnapshotCard(weather: Weather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "WEATHER SNAPSHOT",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        weather.cityName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        weather.condition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Text(
                    "${weather.temperature.toInt()}°C",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
fun ImagePreviewArea(
    imagePath: String?,
    originalSize: Long,
    compressedSize: Long,
    onCaptureClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imagePath == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.White.copy(alpha = 0.1f), MaterialTheme.shapes.extraLarge)
                    .clickable { onCaptureClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Tap to Capture Photo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column {
                    AsyncImage(
                        model = imagePath,
                        contentDescription = "Captured Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Original: $originalSize KB",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            "Compressed: $compressedSize KB",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayTop,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            TextButton(
                onClick = onCaptureClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Retake Photo", fontWeight = FontWeight.Bold)
            }
        }
    }
}
