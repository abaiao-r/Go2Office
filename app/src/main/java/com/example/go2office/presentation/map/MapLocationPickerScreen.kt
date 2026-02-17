package com.example.go2office.presentation.map

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.go2office.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationPickerScreen(
    viewModel: MapLocationPickerViewModel = hiltViewModel(),
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    onLocationSelected: (Double, Double, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val defaultOfficeName = stringResource(R.string.default_office_name)

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName

        if (initialLatitude != null && initialLongitude != null) {
            viewModel.setSelectedLocation(initialLatitude, initialLongitude)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pick_location_on_map)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    if (uiState.selectedLatitude != null && uiState.selectedLongitude != null) {
                        TextButton(
                            onClick = {
                                onLocationSelected(
                                    uiState.selectedLatitude!!,
                                    uiState.selectedLongitude!!,
                                    uiState.selectedLocationName ?: defaultOfficeName
                                )
                            }
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            var mapView by remember { mutableStateOf<MapView?>(null) }

            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)

                        val startPoint = if (initialLatitude != null && initialLongitude != null) {
                            GeoPoint(initialLatitude, initialLongitude)
                        } else {
                            GeoPoint(38.7223, -9.1393)
                        }
                        controller.setCenter(startPoint)

                        val mapEventsReceiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                viewModel.setSelectedLocation(p.latitude, p.longitude)
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        }
                        overlays.add(0, MapEventsOverlay(mapEventsReceiver))

                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.overlays.removeAll { it is Marker }

                    if (uiState.selectedLatitude != null && uiState.selectedLongitude != null) {
                        val marker = Marker(view).apply {
                            position = GeoPoint(uiState.selectedLatitude!!, uiState.selectedLongitude!!)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = uiState.selectedLocationName ?: "Selected Location"
                        }
                        view.overlays.add(marker)
                    }

                    view.invalidate()
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SearchBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.searchLocation() },
                    onClear = { viewModel.clearSearch() },
                    isLoading = uiState.isSearching,
                    searchResults = uiState.searchResults,
                    onResultSelected = { result ->
                        viewModel.selectSearchResult(result)
                        mapView?.controller?.setCenter(GeoPoint(result.latitude, result.longitude))
                        mapView?.controller?.setZoom(17.0)
                    }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { mapView?.controller?.zoomIn() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
                FloatingActionButton(
                    onClick = { mapView?.controller?.zoomOut() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }
                FloatingActionButton(
                    onClick = { viewModel.useCurrentLocation() },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text("📍", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (uiState.selectedLatitude != null && uiState.selectedLongitude != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .padding(bottom = 80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.selected_location),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = uiState.selectedLocationName ?: stringResource(R.string.unknown_location),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(
                                R.string.location_coords_format,
                                uiState.selectedLatitude!!,
                                uiState.selectedLongitude!!
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text(stringResource(R.string.error)) },
            text = { Text(uiState.errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    isLoading: Boolean,
    searchResults: List<SearchResult>,
    onResultSelected: (SearchResult) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(searchResults) {
        expanded = searchResults.isNotEmpty()
    }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChange(it)
                expanded = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(stringResource(R.string.search_place_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                Row {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                        }
                    }
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (expanded && searchResults.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 250.dp)
                ) {
                    items(searchResults) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = {
                                onResultSelected(result)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "📍",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.displayName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Lat: %.4f, Lon: %.4f".format(result.latitude, result.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider()
}

data class SearchResult(
    val displayName: String,
    val latitude: Double,
    val longitude: Double
)
