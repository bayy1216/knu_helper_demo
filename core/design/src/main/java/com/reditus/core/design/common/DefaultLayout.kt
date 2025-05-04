package com.reditus.core.design.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.reditus.core.design.KnuTheme
import com.reditus.core.design.KnuThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultLayout(
    title: String? = null,
    topAppBarHeight : Dp = 56.dp,
    navigationIcon: @Composable () -> Unit = {},
    rightActions: @Composable RowScope.() -> Unit = {},
    body: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            if(title != null) {
                Surface(
                    shadowElevation = 2.dp,
                ) {
                    /**
                     * Because of Modifier's height([topAppBarHeight])
                     * use Box to center the title
                     */
                    TopAppBar(
                        modifier = Modifier
                            .height(topAppBarHeight)
                            .fillMaxSize(),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                        ),
                        navigationIcon = navigationIcon,
                        title = {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = title,
                                    style = KnuThemes.typography.titleLarge,
                                )
                            }

                        },
                        actions =  {
                            Row(
                                modifier = Modifier.fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                rightActions()
                            }
                        }
                    )
                }
            }
        },
    ) {
        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(Color.White)
        ) {
            body()
        }
    }

}


@Preview
@Composable
private fun DefaultLayoutPreview() {
    KnuTheme {
        DefaultLayout(
            title = "Title",
            navigationIcon = {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black,
                    )
                }
            },
            rightActions ={
                Text(text = "Actions")
            }
        ) {
            Text(text = "Content")
        }
    }

}