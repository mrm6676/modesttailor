package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.FashionCatalogConstants
import com.example.data.model.InventoryItem
import com.example.data.model.StyleProfile
import com.example.ui.viewmodel.DataUiState
import com.example.util.SizeMatchingHelper

private const val TOTAL_WIZARD_STEPS = 4

@Composable
fun ModestyProfileScreen(
    profileState: DataUiState<StyleProfile>,
    userEmail: String?,
    inStockInventory: List<InventoryItem> = emptyList(),
    isSavingProfile: Boolean = false,
    isAuthenticated: Boolean = true,
    onSignInClick: () -> Unit = {},
    onSaveProfile: (StyleProfile, onSaved: () -> Unit) -> Unit,
    onNavigateToStylist: () -> Unit = {},
    onSignOut: () -> Unit
) {
    val loadedProfile = (profileState as? DataUiState.Success)?.data

    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var displayName by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("Ladies & Girls") }
    var cityOrArea by remember { mutableStateOf("") }
    var topSize by remember { mutableStateOf("M") }
    var bottomSize by remember { mutableStateOf("M") }
    var dressAbayaLength by remember { mutableStateOf("54") }
    var girlsSize by remember { mutableStateOf("Girls 10-12") }
    var necklineRule by remember { mutableStateOf(FashionCatalogConstants.NECKLINE_OPTIONS.first()) }
    var sleeveRule by remember { mutableStateOf(FashionCatalogConstants.SLEEVE_OPTIONS.first()) }
    var hemlineRule by remember { mutableStateOf(FashionCatalogConstants.HEMLINE_OPTIONS.first()) }
    var fitRule by remember { mutableStateOf(FashionCatalogConstants.FIT_OPTIONS.first()) }
    var hijabFriendly by remember { mutableStateOf(true) }
    var selectedTrends by remember { mutableStateOf(setOf<String>()) }
    var selectedColors by remember { mutableStateOf(setOf<String>()) }

    var showNameError by remember { mutableStateOf(false) }
    var stepThreeValidationHint by remember { mutableStateOf<String?>(null) }
    var savedConfirmationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loadedProfile) {
        if (loadedProfile != null) {
            displayName = loadedProfile.displayName
            audience = loadedProfile.audience
            cityOrArea = loadedProfile.cityOrArea
            topSize = loadedProfile.topSize.ifBlank { "M" }
            bottomSize = loadedProfile.bottomSize.ifBlank { "M" }
            dressAbayaLength = loadedProfile.dressAbayaLength.ifBlank { "54" }
            girlsSize = loadedProfile.girlsSize.ifBlank { "Girls 10-12" }
            necklineRule = loadedProfile.necklineRule
            sleeveRule = loadedProfile.sleeveRule
            hemlineRule = loadedProfile.hemlineRule
            fitRule = loadedProfile.fitRule
            hijabFriendly = loadedProfile.hijabFriendly
            selectedTrends = loadedProfile.trendTags.toSet()
            selectedColors = loadedProfile.favoriteColors.toSet()
        }
    }

    // Intercept system back button inside multi-step wizard before leaving screen
    BackHandler(enabled = currentStep > 0) {
        currentStep = (currentStep - 1).coerceAtLeast(0)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (currentStep + 1) / TOTAL_WIZARD_STEPS.toFloat(),
        label = "wizard_progress"
    )

    val stepLabels = listOf(
        stringResource(R.string.profile_step_identity),
        stringResource(R.string.profile_step_coverage),
        stringResource(R.string.profile_step_palette),
        stringResource(R.string.profile_step_review)
    )

    fun validateAndAdvance(targetStep: Int) {
        if (targetStep > 0 && displayName.isBlank()) {
            showNameError = true
            currentStep = 0
            return
        }
        if (targetStep > 2 && (selectedTrends.isEmpty() || selectedColors.isEmpty())) {
            stepThreeValidationHint = "Select at least one modest trend and one preferred color to tailor your lookbook."
            currentStep = 2
            return
        }
        showNameError = false
        stepThreeValidationHint = null
        currentStep = targetStep.coerceIn(0, TOTAL_WIZARD_STEPS - 1)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (profileState) {
            is DataUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
            }

            is DataUiState.Error -> {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = profileState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        OutlinedButton(onClick = onSignOut) {
                            Text(stringResource(R.string.profile_sign_out))
                        }
                    }
                }
            }

            is DataUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_title),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Step ${currentStep + 1} of $TOTAL_WIZARD_STEPS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            Text(
                                text = stringResource(
                                    if (isAuthenticated) R.string.profile_subtitle else R.string.profile_subtitle_guest
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!userEmail.isNullOrBlank()) {
                                Text(
                                    text = "Google Account: $userEmail",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Interactive Stepper Progress Header
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                stepLabels.forEachIndexed { index, label ->
                                    val isActive = index == currentStep
                                    val isCompleted = index < currentStep
                                    Surface(
                                        color = when {
                                            isActive -> MaterialTheme.colorScheme.primary
                                            isCompleted -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        },
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(MaterialTheme.shapes.medium)
                                            .border(
                                                width = 1.dp,
                                                color = if (isActive) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.outlineVariant
                                                },
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .clickable { validateAndAdvance(index) }
                                            .minimumInteractiveComponentSize()
                                            .testTag("wizard_tab_$index")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = "${index + 1}. $label",
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = when {
                                                    isActive -> MaterialTheme.colorScheme.onPrimary
                                                    isCompleted -> MaterialTheme.colorScheme.onSecondaryContainer
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Active Step Form Content
                    item {
                        AnimatedContent(
                            targetState = currentStep,
                            label = "profile_wizard_step_transition"
                        ) { step ->
                            when (step) {
                                0 -> {
                                    val draftProfile = profileState.data.copy(
                                        audience = audience,
                                        topSize = topSize,
                                        bottomSize = bottomSize,
                                        dressAbayaLength = dressAbayaLength,
                                        girlsSize = girlsSize
                                    )
                                    val matchingCount = remember(
                                        inStockInventory,
                                        audience,
                                        topSize,
                                        bottomSize,
                                        dressAbayaLength,
                                        girlsSize
                                    ) {
                                        SizeMatchingHelper.partitionInventoryBySize(
                                            inStockInventory,
                                            draftProfile
                                        ).matchingItems.size
                                    }
                                    WizardIdentityStep(
                                        displayName = displayName,
                                        onDisplayNameChange = {
                                            displayName = it
                                            if (it.isNotBlank()) showNameError = false
                                            savedConfirmationVisible = false
                                        },
                                        showNameError = showNameError,
                                        cityOrArea = cityOrArea,
                                        onCityOrAreaChange = {
                                            cityOrArea = it
                                            savedConfirmationVisible = false
                                        },
                                        audience = audience,
                                        onAudienceChange = {
                                            audience = it
                                            savedConfirmationVisible = false
                                        },
                                        topSize = topSize,
                                        onTopSizeChange = {
                                            topSize = it
                                            savedConfirmationVisible = false
                                        },
                                        bottomSize = bottomSize,
                                        onBottomSizeChange = {
                                            bottomSize = it
                                            savedConfirmationVisible = false
                                        },
                                        dressAbayaLength = dressAbayaLength,
                                        onDressAbayaLengthChange = {
                                            dressAbayaLength = it
                                            savedConfirmationVisible = false
                                        },
                                        girlsSize = girlsSize,
                                        onGirlsSizeChange = {
                                            girlsSize = it
                                            savedConfirmationVisible = false
                                        },
                                        matchingInventoryCount = matchingCount,
                                        totalInStockCount = inStockInventory.size,
                                        isSavingProfile = isSavingProfile,
                                        onQuickSaveSizes = {
                                            val updated = profileState.data.copy(
                                                displayName = displayName.ifBlank { "Modest Stylist" },
                                                audience = audience,
                                                cityOrArea = cityOrArea.ifBlank { "Local Boutique District" },
                                                topSize = topSize,
                                                bottomSize = bottomSize,
                                                dressAbayaLength = dressAbayaLength,
                                                girlsSize = girlsSize,
                                                necklineRule = necklineRule,
                                                sleeveRule = sleeveRule,
                                                hemlineRule = hemlineRule,
                                                fitRule = fitRule,
                                                hijabFriendly = hijabFriendly,
                                                trendTags = selectedTrends.toList(),
                                                favoriteColors = selectedColors.toList()
                                            )
                                            onSaveProfile(updated) {
                                                savedConfirmationVisible = true
                                            }
                                        }
                                    )
                                }

                                1 -> WizardCoverageStep(
                                    necklineRule = necklineRule,
                                    onNecklineChange = {
                                        necklineRule = it
                                        savedConfirmationVisible = false
                                    },
                                    sleeveRule = sleeveRule,
                                    onSleeveChange = {
                                        sleeveRule = it
                                        savedConfirmationVisible = false
                                    },
                                    hemlineRule = hemlineRule,
                                    onHemlineChange = {
                                        hemlineRule = it
                                        savedConfirmationVisible = false
                                    },
                                    fitRule = fitRule,
                                    onFitChange = {
                                        fitRule = it
                                        savedConfirmationVisible = false
                                    },
                                    hijabFriendly = hijabFriendly,
                                    onHijabFriendlyChange = {
                                        hijabFriendly = it
                                        savedConfirmationVisible = false
                                    }
                                )

                                2 -> WizardStyleAndPaletteStep(
                                    selectedTrends = selectedTrends,
                                    onToggleTrend = { tag ->
                                        savedConfirmationVisible = false
                                        stepThreeValidationHint = null
                                        selectedTrends = if (tag in selectedTrends) {
                                            selectedTrends - tag
                                        } else if (selectedTrends.size < 10) {
                                            selectedTrends + tag
                                        } else {
                                            selectedTrends
                                        }
                                    },
                                    selectedColors = selectedColors,
                                    onToggleColor = { colorName ->
                                        savedConfirmationVisible = false
                                        stepThreeValidationHint = null
                                        selectedColors = if (colorName in selectedColors) {
                                            selectedColors - colorName
                                        } else if (selectedColors.size < 10) {
                                            selectedColors + colorName
                                        } else {
                                            selectedColors
                                        }
                                    },
                                    validationHint = stepThreeValidationHint
                                )

                                else -> WizardReviewStep(
                                    displayName = displayName,
                                    cityOrArea = cityOrArea,
                                    audience = audience,
                                    topSize = topSize,
                                    bottomSize = bottomSize,
                                    dressAbayaLength = dressAbayaLength,
                                    girlsSize = girlsSize,
                                    necklineRule = necklineRule,
                                    sleeveRule = sleeveRule,
                                    hemlineRule = hemlineRule,
                                    fitRule = fitRule,
                                    hijabFriendly = hijabFriendly,
                                    selectedTrends = selectedTrends,
                                    selectedColors = selectedColors,
                                    savedConfirmationVisible = savedConfirmationVisible,
                                    onJumpToStep = { target -> currentStep = target },
                                    onNavigateToStylist = onNavigateToStylist
                                )
                            }
                        }
                    }

                    // Multi-Step Navigation & Firestore Persist Controls
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (currentStep > 0) {
                                    OutlinedButton(
                                        onClick = { currentStep -= 1 },
                                        shape = MaterialTheme.shapes.large,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                            .testTag("wizard_back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(stringResource(R.string.profile_step_back))
                                    }
                                }

                                if (currentStep < TOTAL_WIZARD_STEPS - 1) {
                                    Button(
                                        onClick = { validateAndAdvance(currentStep + 1) },
                                        shape = MaterialTheme.shapes.large,
                                        modifier = Modifier
                                            .weight(1.4f)
                                            .height(54.dp)
                                            .testTag("wizard_next_button")
                                    ) {
                                        Text(
                                            text = stringResource(R.string.profile_step_next),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            val updated = profileState.data.copy(
                                                displayName = displayName.ifBlank { "Modest Stylist" },
                                                audience = audience,
                                                topSize = topSize,
                                                bottomSize = bottomSize,
                                                dressAbayaLength = dressAbayaLength,
                                                girlsSize = girlsSize,
                                                necklineRule = necklineRule,
                                                sleeveRule = sleeveRule,
                                                hemlineRule = hemlineRule,
                                                fitRule = fitRule,
                                                hijabFriendly = hijabFriendly,
                                                cityOrArea = cityOrArea.ifBlank { "Local Boutique District" },
                                                trendTags = selectedTrends.toList(),
                                                favoriteColors = selectedColors.toList()
                                            )
                                            onSaveProfile(updated) {
                                                savedConfirmationVisible = true
                                            }
                                        },
                                        enabled = !isSavingProfile,
                                        shape = MaterialTheme.shapes.large,
                                        modifier = Modifier
                                            .weight(1.6f)
                                            .height(54.dp)
                                            .testTag("save_profile_button")
                                    ) {
                                        if (isSavingProfile) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                strokeWidth = 2.5.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(R.string.profile_saving_cta))
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = stringResource(
                                                    if (isAuthenticated) {
                                                        R.string.profile_save_cta
                                                    } else {
                                                        R.string.profile_save_cta_guest
                                                    }
                                                ),
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                    }
                                }
                            }

                            if (isAuthenticated) {
                                OutlinedButton(
                                    onClick = onSignOut,
                                    shape = MaterialTheme.shapes.large,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("sign_out_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                                        contentDescription = stringResource(R.string.cd_sign_out),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.profile_sign_out))
                                }
                            } else {
                                FilledTonalButton(
                                    onClick = onSignInClick,
                                    shape = MaterialTheme.shapes.large,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("profile_sign_in_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = stringResource(R.string.cd_sign_in_account),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.profile_sign_in_cta))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
