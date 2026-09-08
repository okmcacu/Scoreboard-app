package com.gamezone.scoreboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamezone.scoreboard.ScoreboardViewModel
import com.gamezone.scoreboard.ui.theme.GoldColor
import com.gamezone.scoreboard.ui.theme.GreenColor
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RedColor
import com.gamezone.scoreboard.ui.theme.Surface2Color

private val RajdhaniFont = FontFamily.Monospace

@Composable
fun GamesScreen(viewModel: ScoreboardViewModel) {
    var newCatName by remember { mutableStateOf("") }
    var newCatPrice by remember { mutableStateOf("20") }
    var newPlayerName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)
    ) {
        // GAMES SECTION
        item {
            Text(
                text = "GAMES",
                fontFamily = RajdhaniFont,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MutedColor,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    viewModel.categories.forEachIndexed { index, c ->
                        var priceText by remember(c.id) { mutableStateOf(c.price.toString()) }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = c.name,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Compact Price Edit Input (.price-edit)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Surface2Color)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BasicTextField(
                                    value = priceText,
                                    onValueChange = {
                                        priceText = it
                                        viewModel.updateCategoryPrice(c.id, it.toIntOrNull() ?: 0)
                                    },
                                    modifier = Modifier.width(46.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = GoldColor,
                                        fontFamily = RajdhaniFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.End
                                    ),
                                    cursorBrush = SolidColor(GoldColor),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Text(
                                    text = "Birr",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedColor
                                )
                            }

                            TextButton(
                                onClick = { viewModel.removeCategory(c.id) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "REMOVE",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RedColor
                                )
                            }
                        }
                        if (index < viewModel.categories.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Inline Add Game (.inline-add)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomDarkInput(
                            value = newCatName,
                            onValueChange = { newCatName = it },
                            placeholder = "Game name",
                            modifier = Modifier.weight(1f)
                        )
                        CustomDarkInput(
                            value = newCatPrice,
                            onValueChange = { newCatPrice = it },
                            placeholder = "Birr",
                            modifier = Modifier.width(80.dp),
                            textColor = GoldColor,
                            keyboardType = KeyboardType.Number
                        )
                        Button(
                            onClick = {
                                viewModel.addCategory(newCatName, newCatPrice.toIntOrNull() ?: 20)
                                newCatName = ""
                                newCatPrice = "20"
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenColor,
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "ADD",
                                fontFamily = RajdhaniFont,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // PLAYERS SECTION
        item {
            Text(
                text = "PLAYERS",
                fontFamily = RajdhaniFont,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MutedColor,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    viewModel.players.forEachIndexed { index, p ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = p.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(
                                onClick = { viewModel.removePlayer(p.id) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "REMOVE",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RedColor
                                )
                            }
                        }
                        if (index < viewModel.players.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Inline Add Player (.inline-add)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomDarkInput(
                            value = newPlayerName,
                            onValueChange = { newPlayerName = it },
                            placeholder = "Add a player",
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                viewModel.addPlayer(newPlayerName)
                                newPlayerName = ""
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenColor,
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "ADD",
                                fontFamily = RajdhaniFont,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDarkInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Surface2Color)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = MutedColor, fontSize = 14.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 14.sp,
                fontFamily = if (keyboardType == KeyboardType.Number) RajdhaniFont else FontFamily.Default,
                fontWeight = if (keyboardType == KeyboardType.Number) FontWeight.Bold else FontWeight.Normal
            ),
            cursorBrush = SolidColor(textColor),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
    }
}