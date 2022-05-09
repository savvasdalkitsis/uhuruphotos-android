/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.people.api.view.PersonImage
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction

internal fun LazyListScope.personSuggestion(
    person: Person,
    action: (SearchAction) -> Unit,
) {
    item(person.name, contentType = "personSuggestion") {
        Suggestion(
            modifier = Modifier.animateItemPlacement(),
            text = person.name,
            onClick = {
                action(SearchAction.PersonSelected(person))
            },
            leadingContent = {
                PersonImage(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    person = person
                )
            }
        )
    }
}