package com.pirate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirate.R
import com.pirate.ui.theme.AppBackground
import com.pirate.viewModels.MainViewModel

@Composable
fun BasicTopBar(
    title: String,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val backIcon = R.drawable.icon_arrow_left
    val iconSize = 20.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(AppBackground)
            .padding(start = 24.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = {
            mainViewModel.navController.popBackStack()
        }) {
            Icon(
                painter = painterResource(id = backIcon),
                contentDescription = "backIcon",
                modifier = Modifier
                    .size(iconSize)
                    .clip(shape = CircleShape),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}
