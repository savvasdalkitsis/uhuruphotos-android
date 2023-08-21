/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.R
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.Help
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.HideNeedsAccess
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.HidePermissionRationale
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.LogOut
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.NavigateToAppSettings
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.Save
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.SelectCloudMedia
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.ShowNeedsAccess
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.ShowPermissionRationale
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.AppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.DynamicIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.MultiButtonDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.TextWithIcon
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
internal fun Welcome(
    state: WelcomeState,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = rememberPermissionFlowRequestLauncher(),
    action: (WelcomeAction) -> Unit,
) {
    CommonScaffold(
        title = { Logo() },
        actionBarContent = {
            ActionIcon(
                onClick = { action(Help) },
                icon = drawable.ic_help,
            )
        }
    ) { contentPadding ->
        val permissionState = state.missingPermissions?.let {
            rememberMultiplePermissionsState(it)
        }
        Column(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = 24.dp,
                    end = 24.dp,
                    bottom = contentPadding.calculateBottomPadding() + 24.dp,
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = spacedBy(18.dp)
        ) {
            Text(
                text = stringResource(string.welcome_to_uhuruphotos),
                style = MaterialTheme.typography.h4,
            )
            Text(
                text = stringResource(string.welcome_description),
                style = MaterialTheme.typography.body1,
            )
            Text(
                text = stringResource(string.welcome_description_2),
                style = MaterialTheme.typography.body2,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(string.what_would_like_to_do),
                style = MaterialTheme.typography.h5,
            )
            Spacer(modifier = Modifier.weight(1f))

            when {
                state.isLoading -> FullLoading()
                else -> Column(
                    verticalArrangement = spacedBy(8.dp)
                ) {
                    if (state.cloudMediaSelected) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = spacedBy(32.dp),
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = { action(LogOut) }
                            ) {
                                Text(stringResource(string.log_out))
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.sizeIn(maxHeight = 240.dp),
                        horizontalArrangement = spacedBy(32.dp),
                    ) {
                        UseCase(
                            R.raw.animation_local_media,
                            string.manage_media_on_device,
                            state.localMediaSelected,
                        ) {
                            permissionState?.let {
                                if (it.shouldShowRationale) {
                                    action(ShowPermissionRationale)
                                } else if (!it.allPermissionsGranted) {
                                    action(ShowNeedsAccess)
                                }
                            }
                        }
                        UseCase(
                            if (state.localMediaSelected) R.raw.animation_cloud_backup else R.raw.animation_cloud,
                            if (state.localMediaSelected) string.backup_media_on_cloud else string.manage_media_on_cloud,
                            state.cloudMediaSelected,
                        ) {
                            action(SelectCloudMedia)
                        }
                    }
                }
            }
            Text(
                text = stringResource(string.welcome_footnote),
                style = MaterialTheme.typography.caption,
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSaveEnabled,
                onClick = { action(Save) }
            ) {
                Text(text = stringResource(string.save))
            }
        }
        if (state.showPermissionRationale) {
            MultiButtonDialog(
                title = stringResource(string.missing_permissions),
                onDismiss = { action(HidePermissionRationale) },
                confirmButton = {
                    Button(onClick = {
                        action(HidePermissionRationale)
                        state.missingPermissions?.let {
                            permissionLauncher?.launch(it.toTypedArray())
                        }
                    }) {
                        Text(stringResource(string.ok))
                    }
                },
                negativeButtonText = stringResource(string.cancel)
            ) {
                Text(stringResource(string.need_permissions_to_manage_gallery))
            }
        }
        if (state.showNeedsAccess) {
            MultiButtonDialog(
                title = stringResource(string.missing_permissions),
                onDismiss = { action(HideNeedsAccess) },
                extraButtons = listOf {
                    OutlinedButton(onClick = {
                        action(HideNeedsAccess)
                        action(NavigateToAppSettings)
                    }) {
                        Text(stringResource(string.navigate_to_settings))
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        action(HideNeedsAccess)
                        state.missingPermissions?.let {
                            permissionLauncher?.launch(it.toTypedArray())
                        }
                    }) {
                        Text(stringResource(string.attempt_grant_permissions))
                    }
                },
                negativeButtonText = stringResource(string.cancel)
            ) {
                Text(stringResource(string.need_permissions_to_manage_gallery))
            }
        }
    }
}

@Composable
private fun RowScope.UseCase(
    @RawRes icon: Int,
    @StringRes text: Int,
    selected: Boolean,
    tint: Color? = null,
    onClick: () -> Unit,
) {
    AppTheme(darkTheme = false) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            elevation = 4.dp
        ) {
            ConstraintLayout(modifier = Modifier
                .clickable(enabled = !selected) { onClick() }
                .padding(8.dp)
            ) {
                val (iconC, textC) = createRefs()

                TextWithIcon(
                    modifier = Modifier.constrainAs(textC) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    iconModifier = Modifier.size(24.dp),
                    icon = if (selected) drawable.ic_outline_selected else drawable.ic_outline_unselected,
                    tint = if (selected) CustomColors.selected else null,
                    text = stringResource(text),
                )
                DynamicIcon(
                    modifier = Modifier.constrainAs(iconC) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(textC.top)
                    },
                    tint = tint,
                    icon = icon,
                )
            }
        }
    }
}

@Preview
@Composable
private fun WelcomePreview() {
    PreviewAppTheme {
        Welcome(
            state = WelcomeState(localMediaSelected = true, isLoading = false),
            permissionLauncher = null,
        ) {}
    }
}
@Preview
@Composable
private fun WelcomePreviewDark() {
    PreviewAppTheme(darkTheme = true) {
        Welcome(
            state = WelcomeState(localMediaSelected = true, isLoading = false),
            permissionLauncher = null,
        ) {}
    }
}