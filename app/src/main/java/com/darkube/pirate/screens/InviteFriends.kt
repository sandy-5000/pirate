package com.darkube.pirate.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.R.drawable.share_icon
import com.darkube.pirate.ui.theme.PrimaryColor

@Composable
fun InviteFriends(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val context = LocalContext.current
    var shareLink by remember { mutableStateOf("https://www.example.com") }
    val shareIcon = share_icon
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareLink)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)


    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 50.dp)
            .padding(top = 20.dp)
    ) {
        OutlinedTextField(
            value = shareLink, onValueChange = { shareLink = it },
            placeholder = { Text("Invite Friends") },
            shape = RoundedCornerShape(4.dp)
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                context.startActivity(shareIntent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(
                painter = painterResource(id = shareIcon),
                contentDescription = "Share",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Share",
                color = Color.White,
            )
        }
    }
}