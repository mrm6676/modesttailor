package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiGarmentScanResult
import com.example.data.model.FashionCatalogConstants
import com.example.data.model.InventoryItem
import com.example.ui.viewmodel.DataUiState
import com.example.ui.viewmodel.GarmentScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BoutiqueInventoryScreen(
    inventoryState: DataUiState<List<InventoryItem>>,
    garmentScanState: GarmentScanUiState,
    defaultDistrict: String,
    onAddItem: (InventoryItem) -> Unit,
    onToggleStock: (itemId: String, inStock: Boolean) -> Unit,
    onDeleteItem: (itemId: String) -> Unit,
    onSeedStarterInventory: () -> Unit,
    onAnalyzeGarmentBitmap: (android.graphics.Bitmap) -> Unit,
    onClearGarmentScan: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var scannedPrefill by remember { mutableStateOf<AiGarmentScanResult?>(null) }

    // Zero-permission Android Photo Picker (Play Policy compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                if (bitmap != null) {
                    onAnalyzeGarmentBitmap(bitmap)
                }
            }
        }
    }

    LaunchedEffect(garmentScanState) {
        if (garmentScanState is GarmentScanUiState.Success) {
            scannedPrefill = garmentScanState.result
            showAddDialog = true
        }
    }

    val filterOptions = listOf(
        "All" to stringResource(R.string.inventory_filter_all),
        "Ladies" to stringResource(R.string.inventory_filter_ladies),
        "Girls" to stringResource(R.string.inventory_filter_girls),
        "Teens" to stringResource(R.string.inventory_filter_teens)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.inventory_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.inventory_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Ladies & Girls Visual Department Banners
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DepartmentBannerCard(
                        imageRes = R.drawable.img_ladies_collection_1793445197717,
                        title = "Ladies Atelier",
                        subtitle = "Maxi, Linen & Abayas",
                        selected = selectedFilter == "Ladies",
                        onClick = {
                            selectedFilter = if (selectedFilter == "Ladies") "All" else "Ladies"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DepartmentBannerCard(
                        imageRes = R.drawable.img_girls_collection_1793445199505,
                        title = "Girls & Teens",
                        subtitle = "Tiered & Playful Modest",
                        selected = selectedFilter == "Girls",
                        onClick = {
                            selectedFilter = if (selectedFilter == "Girls") "All" else "Girls"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Multimodal Garment Photo Scan Action + Audience Filter Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        enabled = garmentScanState !is GarmentScanUiState.Scanning,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("scan_garment_photo_button")
                    ) {
                        if (garmentScanState is GarmentScanUiState.Scanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(stringResource(R.string.inventory_scanning_photo))
                        } else {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.inventory_scan_photo))
                        }
                    }

                    if (garmentScanState is GarmentScanUiState.Error) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = garmentScanState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = onClearGarmentScan) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterOptions) { (key, label) ->
                            FilterChip(
                                selected = selectedFilter == key,
                                onClick = { selectedFilter = key },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            when (inventoryState) {
                is DataUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is DataUiState.Error -> {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = inventoryState.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                is DataUiState.Success -> {
                    val filteredItems = if (selectedFilter == "All") {
                        inventoryState.data
                    } else {
                        inventoryState.data.filter { it.audience == selectedFilter }
                    }

                    if (filteredItems.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = MaterialTheme.shapes.extraLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("empty_inventory_state")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape,
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Checkroom,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = stringResource(R.string.inventory_empty_title),
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = stringResource(R.string.inventory_empty_body),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = onSeedStarterInventory,
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("empty_seed_boutique_button")
                                    ) {
                                        Text(stringResource(R.string.atelier_seed_starter_cta))
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredItems, key = { it.id }) { item ->
                            InventoryGarmentCard(
                                item = item,
                                onToggleStock = { checked -> onToggleStock(item.id, checked) },
                                onDelete = { onDeleteItem(item.id) }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                scannedPrefill = null
                showAddDialog = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_inventory_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.inventory_add_item)
                )
                Text(
                    text = stringResource(R.string.inventory_add_item),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (showAddDialog) {
        AddGarmentDialog(
            prefill = scannedPrefill,
            defaultDistrict = defaultDistrict,
            onDismiss = {
                showAddDialog = false
                scannedPrefill = null
                onClearGarmentScan()
            },
            onSave = { newItem ->
                onAddItem(newItem)
                showAddDialog = false
                scannedPrefill = null
                onClearGarmentScan()
            }
        )
    }
}

@Composable
private fun DepartmentBannerCard(
    imageRes: Int,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 5.dp else 1.dp),
        modifier = modifier.height(115.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0x22000000), Color(0xCC1E1216))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun InventoryGarmentCard(
    item: InventoryItem,
    onToggleStock: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ) {
                        Text(
                            text = item.audience,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = if (item.priceUsd <= 0.0) "Closet Piece" else "$${item.priceUsd.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${item.boutiqueName} • ${item.sizeRange} • ${item.color}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = item.coverageSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    shape = CircleShape
                ) {
                    Text(
                        text = item.trendTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (item.inStock) {
                            stringResource(R.string.inventory_in_stock)
                        } else {
                            stringResource(R.string.inventory_out_of_stock)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (item.inStock) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = item.inStock,
                        onCheckedChange = onToggleStock,
                        modifier = Modifier.testTag("stock_switch_${item.id}")
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_inventory_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(R.string.cd_delete_item),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddGarmentDialog(
    prefill: AiGarmentScanResult?,
    defaultDistrict: String,
    onDismiss: () -> Unit,
    onSave: (InventoryItem) -> Unit
) {
    var name by rememberSaveable(prefill) { mutableStateOf(prefill?.suggestedName.orEmpty()) }
    var audience by rememberSaveable(prefill) {
        mutableStateOf(
            prefill?.audience?.takeIf { it in FashionCatalogConstants.ITEM_AUDIENCE_OPTIONS }
                ?: "Ladies"
        )
    }
    var category by rememberSaveable(prefill) {
        mutableStateOf(
            prefill?.category?.takeIf { it in FashionCatalogConstants.CATEGORY_OPTIONS }
                ?: "Dress & Abaya"
        )
    }
    var boutiqueName by rememberSaveable { mutableStateOf("Atelier Noor") }
    var neighborhood by rememberSaveable { mutableStateOf(defaultDistrict) }
    var priceText by rememberSaveable { mutableStateOf("68") }
    var color by rememberSaveable(prefill) { mutableStateOf(prefill?.color ?: "Rosewood") }
    var sizeRange by rememberSaveable { mutableStateOf("S - XL") }
    var coverageSummary by rememberSaveable(prefill) {
        mutableStateOf(prefill?.coverageSummary ?: "High neckline, full wrist sleeves, opaque ankle-length drape")
    }
    var trendTag by rememberSaveable(prefill) {
        mutableStateOf(prefill?.trendTag ?: "Quiet Luxury Linen")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (prefill != null) "AI-Analyzed Modest Piece" else "Add Local Boutique or Closet Piece",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (prefill != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = prefill.stylingTip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.form_garment_name)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("garment_name_input")
                )

                Text("Wearer Category", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FashionCatalogConstants.ITEM_AUDIENCE_OPTIONS.forEach { option ->
                        FilterChip(
                            selected = audience == option,
                            onClick = { audience = option },
                            label = { Text(option) }
                        )
                    }
                }

                Text("Apparel Type", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(FashionCatalogConstants.CATEGORY_OPTIONS) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                OutlinedTextField(
                    value = boutiqueName,
                    onValueChange = { boutiqueName = it },
                    label = { Text(stringResource(R.string.form_boutique_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text(stringResource(R.string.form_price)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text(stringResource(R.string.form_color)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sizeRange,
                        onValueChange = { sizeRange = it },
                        label = { Text(stringResource(R.string.form_size_range)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text(stringResource(R.string.form_neighborhood)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = coverageSummary,
                    onValueChange = { coverageSummary = it },
                    label = { Text(stringResource(R.string.form_coverage_summary)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = trendTag,
                    onValueChange = { trendTag = it },
                    label = { Text(stringResource(R.string.form_trend_tag)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedPrice = priceText.toDoubleOrNull()?.coerceIn(0.0, 50000.0) ?: 0.0
                    onSave(
                        InventoryItem(
                            name = name.ifBlank { "Modest Apparel Piece" },
                            category = category,
                            audience = audience,
                            boutiqueName = boutiqueName.ifBlank { "Personal Closet" },
                            neighborhood = neighborhood,
                            priceUsd = parsedPrice,
                            color = color.ifBlank { "Neutral" },
                            sizeRange = sizeRange.ifBlank { "One Size" },
                            coverageSummary = coverageSummary,
                            trendTag = trendTag.ifBlank { "Contemporary Modest" },
                            inStock = true
                        )
                    )
                },
                modifier = Modifier.testTag("confirm_save_garment_button")
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
