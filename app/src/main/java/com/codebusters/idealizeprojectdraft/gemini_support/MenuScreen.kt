/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codebusters.idealizeprojectdraft.gemini_support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.codebusters.idealizeprojectdraft.R

data class MenuItem(
    val routeId: String,
    val titleResId: Int,
    val descriptionResId: Int
)

val LocalAppColorPalette = staticCompositionLocalOf {
    AppColorPalette(
        primary = Color.White,
        onPrimary = Color.Black,
        secondary = Color.White,
        onSecondary = Color.Black,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black
    )
}

data class AppColorPalette(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color
)

@Composable
fun GreenTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        AppColorPalette(
            primary = colorResource(id = R.color.colorPrimaryDark),
            onPrimary = Color.White,
            secondary = colorResource(id = R.color.colorSecondary),
            onSecondary = Color.White,
            background = colorResource(id = R.color.colorBackground),
            onBackground = Color.White,
            surface = colorResource(id = R.color.colorBackground),
            onSurface = Color.White
        )
    } else {
        AppColorPalette(
            primary = colorResource(id = R.color.colorPrimaryDark),
            onPrimary = Color.White,
            secondary = colorResource(id = R.color.colorSecondary),
            onSecondary = Color.White,
            background = colorResource(id = R.color.colorBackground),
            onBackground = Color.White,
            surface = colorResource(id = R.color.colorBackground),
            onSurface = Color.White
        )
    }

    CompositionLocalProvider(LocalAppColorPalette provides colors) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = LocalAppColorPalette.current.primary,
                onPrimary = LocalAppColorPalette.current.onPrimary,
                secondary = LocalAppColorPalette.current.secondary,
                onSecondary = LocalAppColorPalette.current.onSecondary,
                background = LocalAppColorPalette.current.background,
                onBackground = LocalAppColorPalette.current.onBackground,
                surface = LocalAppColorPalette.current.surface,
                onSurface = LocalAppColorPalette.current.onSurface
            ),
            content = content
        )
    }
}

@Composable
fun MenuScreen(
    onItemClicked: (String) -> Unit = { }
) {
    val menuItems = listOf(
        //MenuItem("summarize", R.string.menu_summarize_title, R.string.menu_summarize_description),
        MenuItem("photo_reasoning", R.string.menu_reason_title, R.string.menu_reason_description),
        MenuItem("chat", R.string.menu_chat_title, R.string.menu_chat_description)
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
    ){
        LazyColumn(
            Modifier
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            items(menuItems) { menuItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardColors(containerColor = colorResource(id = R.color.lightgreyv2),
                        contentColor = colorResource(id = R.color.colorPrimary),
                        disabledContentColor = colorResource(id = R.color.grey),
                        disabledContainerColor = colorResource(id = R.color.colorSecondary))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(menuItem.titleResId),
                            style = MaterialTheme.typography.titleMedium
                            , fontSize = TextUnit(18f, TextUnitType.Sp)
                            ,color = colorResource(id = R.color.colorPrimaryDark),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight(600)

                        )
                        Text(
                            text = stringResource(menuItem.descriptionResId),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                            , fontSize = TextUnit(15f, TextUnitType.Sp)
                            ,color = colorResource(id = R.color.appgreeen),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight(600)
                        )
                        TextButton(
                            onClick = {
                                onItemClicked(menuItem.routeId)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "Use"
                                , fontSize = TextUnit(18f, TextUnitType.Sp)
                                ,color = colorResource(id = R.color.colorPrimaryDark),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight(600))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    GreenTheme(darkTheme = false) {
        MenuScreen()
    }
}
