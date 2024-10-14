package com.yuchen.composeapp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yuchen.composeapp.R
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.YCColor
import com.yuchen.composeapp.ui.theme.ComposeAppTheme

@Composable
fun MenuView(
    modifier: Modifier = Modifier,
    selectedColor: YCColor,
    onDeleteClicked: () -> Unit,
    onTextClicked: () -> Unit,
    onColorSelected: (YCColor) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row {
            IconButton(onClick = onDeleteClicked) {
                val painter = painterResource(id = R.drawable.ic_delete)
                Icon(painter = painter, contentDescription = "Delete")
            }

            IconButton(onClick = onTextClicked) {
                val painter = painterResource(R.drawable.ic_text)
                Icon(painter, "Edit text")
            }

            IconButton(onClick = { expanded = true }) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(selectedColor.color), shape = CircleShape)
                )

                DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                    YCColor.defaultColors.forEach { color ->
                        DropdownMenuItem(text = {}, onClick = {
                            onColorSelected(color)
                            expanded = false
                        },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(color.color), shape = CircleShape)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuViewPreview() {
    ComposeAppTheme {
        MenuView(Modifier,
            YCColor.Gorse,
            {}, {}, {})
    }
}