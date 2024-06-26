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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


data class MenuItem(
    val routeId: String,
    val titleResId: Int,
    val descriptionResId: Int,
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
    val menu1 = listOf(
        MenuItem("chat", R.string.menu_chat_title, R.string.menu_chat_description)
    )

    val menu2 = listOf(
        MenuItem("photo_reasoning", R.string.menu_reason_title, R.string.menu_reason_description),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 16.dp)
        )  {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(193.dp)
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.AI1),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 46.dp, top = 30.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.robo),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(150.dp)
                        .height(200.dp)
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = stringResource(id = R.string.support),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = colorResource(id = R.color.black)
                    ),
                    modifier = Modifier
                        .padding(start = 25.dp , top = 170.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.frame2),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 0.dp)
                )
            }
            Text(
                text = stringResource(id = R.string.languages),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black)
                ),
                modifier = Modifier
                    .padding(start = 0.dp, top = 0.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(menu1) { menuItem ->
                    Text(
                        text = stringResource(id = R.string.Prompts),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = colorResource(id = R.color.black)
                        ),
                        modifier = Modifier
                            .padding(start = 15.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.tet1),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp, start = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Image(
                        painter = painterResource(id = R.drawable.text2),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp, start = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.End)
                            .padding(start = 180.dp, end = 10.dp),
                                colors = CardColors(
                            containerColor = colorResource(id = R.color.lightgreyv2),
                            contentColor = colorResource(id = R.color.colorPrimary),
                            disabledContentColor = colorResource(id = R.color.grey),
                            disabledContainerColor = colorResource(id = R.color.colorSecondary)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(all = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(bottom = 6.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        onItemClicked(menuItem.routeId)
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.arrowbutton),
                                        contentDescription = "Use",
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.f1), // Replace with your green button image resource
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 16.dp, bottom = 0.dp)
                                )
                            }
                            Text(
                                text = stringResource(menuItem.descriptionResId),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp),
                                fontSize = TextUnit(15f, TextUnitType.Sp),
                                color = colorResource(id = R.color.appgreeen),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight(600)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(menu2) { menuItem ->
                    Text(
                        text = stringResource(id = R.string.Prompts),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = colorResource(id = R.color.black)
                        ),
                        modifier = Modifier
                            .padding(start = 15.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.text3),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp, start = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Image(
                        painter = painterResource(id = R.drawable.text4),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp, start = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .padding(start = 10.dp, end = 180.dp),
                        colors = CardColors(
                            containerColor = colorResource(id = R.color.lightgreyv2),
                            contentColor = colorResource(id = R.color.colorPrimary),
                            disabledContentColor = colorResource(id = R.color.grey),
                            disabledContainerColor = colorResource(id = R.color.colorSecondary)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(all = 16.dp)
                                .fillMaxWidth()
                        ){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(bottom = 6.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        onItemClicked(menuItem.routeId)
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.arrowbutton),
                                        contentDescription = "Use",
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.f2), // Replace with your green button image resource
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 16.dp, bottom = 0.dp)
                                )
                            }
                            Text(
                                text = stringResource(menuItem.descriptionResId),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp),
                                fontSize = TextUnit(15f, TextUnitType.Sp),
                                color = colorResource(id = R.color.appgreeen),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight(600)
                            )
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
