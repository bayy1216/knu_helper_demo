package com.reditus.core.design.notice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reditus.core.design.KnuTheme

@Composable
fun NoticeCard(
    modifier : Modifier = Modifier,
    title: String,
    site1st: String,
    site2nd: String,
    siteColor: Color,
    date: String,
    views: Int,
    favorite: Boolean,
    onClick: () -> Unit,
    favoriteOnClick: () -> Unit,
){
    Column(
        modifier = modifier
            .height(150.dp)
            .clickable { onClick() }
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SiteChip(
                site = site1st,
                siteColor = siteColor,
            )
            Spacer(modifier = Modifier.width(4.dp))
            SiteChip(
                site = site2nd,
                siteColor = Color.Gray,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = date,
                color = Color.Gray,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "조회수 : $views",
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (favorite) Color.Red else Color.Gray,
                modifier = Modifier.clickable { favoriteOnClick() }
            )
        }
    }
}

@Composable
private fun SiteChip(
    site: String,
    siteColor: Color,
) {
    Text(
        text = site,
        color = Color.White,
        modifier = Modifier
            .background(siteColor, shape = CircleShape)
            .padding(vertical = 4.dp, horizontal = 12.dp)
    )
}

@Preview(widthDp = 540)
@Composable
private fun NoticeCardPreview() {
    KnuTheme {
        NoticeCard(
            modifier = Modifier.fillMaxWidth(),
            title = "제목",
            site1st = "경북대",
            site2nd = "경북대-학사공지",
            siteColor = Color.Red,
            date = "2023-10-10",
            views = 100,
            favorite = false,
            onClick = {},
            favoriteOnClick = {}
        )
    }
}