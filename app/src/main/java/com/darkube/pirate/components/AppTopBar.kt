package com.darkube.pirate.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkube.pirate.ui.theme.NavBarBackground
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun AppTopBar() {
    val topPadding = 36.dp
    val barHeight = 68.dp
    val sidesPadding = 18.dp
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(barHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.padding(start = sidesPadding),
        ) {
            Text(text = "Top Bar")
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = sidesPadding),
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(screenWidth * 0.5f)

                    .background(NavBarBackground, shape = RoundedCornerShape(16.dp))
            ) {
                DropdownMenuItem(text = { Text("New group") }, onClick = {
                    Toast.makeText(context, "New group clicked", Toast.LENGTH_SHORT).show()
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Mark all read") }, onClick = {
                    Toast.makeText(context, "Mark all read clicked", Toast.LENGTH_SHORT).show()
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Invite friends") }, onClick = {
                    Toast.makeText(context, "Invite friends clicked", Toast.LENGTH_SHORT).show()
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Filter unread chats") }, onClick = {
                    Toast.makeText(context, "Filter unread chats clicked", Toast.LENGTH_SHORT)
                        .show()
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Settings") }, onClick = {
                    Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
                    expanded = false
                })
                DropdownMenuItem(text = { Text("Notification profile") }, onClick = {
                    Toast.makeText(context, "Notification profile clicked", Toast.LENGTH_SHORT)
                        .show()
                    expanded = false
                })
            }
        }
    }
}
