package br.studyleague.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticsSquare(
    modifier: Modifier = Modifier,
    title: String,
    data: String,
    titleTextStyle: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
    dataTextStyle: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(10.dp))
            .background(Color.White)
            .sizeIn(minWidth = 96.dp, minHeight = 75.dp)
    ) {
        Text(title, style = titleTextStyle)

        Text(data, style = dataTextStyle)
    }
}
