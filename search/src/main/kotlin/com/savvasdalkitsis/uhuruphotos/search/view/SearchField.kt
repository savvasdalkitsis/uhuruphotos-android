package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.ClearSearch
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState

@Composable
fun SearchField(
    state: SearchState,
    action: (SearchAction) -> Unit,
    controllersProvider: ControllersProvider
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .onFocusChanged { focusState ->
                action(SearchAction.ChangeFocus(focusState.isFocused))
            }
            .focusRequester(controllersProvider.focusRequester!!),
        maxLines = 1,
        singleLine = true,
        trailingIcon = {
            AnimatedVisibility(
                visible = state.showClearButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { action(ClearSearch) }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = { action(SearchAction.SearchFor(state.query)) }
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "searchIcon"
            )
        },
        label = { Text("Search for something") },
        value = state.query,
        onValueChange = {
            action(SearchAction.ChangeQuery(it))
        }
    )
}