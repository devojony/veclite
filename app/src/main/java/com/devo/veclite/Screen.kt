package com.devo.veclite

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devo.veclite.ui.theme.VecliteTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    inputState: MutableState<String> = mutableStateOf(""),
    contentList: List<ContentItem> = listOf(),
    onAddContent: (String) -> Unit = {},
    onSearchContent: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    filter: SearchType = SearchType.L2,
    onFilterChange: (SearchType) -> Unit = {}
) {


    Scaffold(
        topBar = {
            AppBar(filter, onFilterChange)
        },
        modifier = Modifier.then(modifier),
        floatingActionButton = { Actions(inputState, onAddContent, onSearchContent, onRefresh) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            InputView(inputState)
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                text = "内容列表",
                style = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contentList.size) {
                    val item = contentList[it]
                    ListItem(item = item)
                    if (it != contentList.size - 1) {
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun Actions(
    inputState: MutableState<String> = mutableStateOf(""),
    onAddContent: (String) -> Unit = {},
    onSearchContent: (String) -> Unit = {},
    onRefresh: () -> Unit = {},

) {
    Column {

        FloatingActionButton(
            onClick = {
                onRefresh()
                inputState.value = ""
            }
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                onAddContent(inputState.value)
                inputState.value = ""
            }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                onSearchContent(inputState.value)
            }
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
    }

}

@Composable
fun InputView(
    inputState: MutableState<String> = mutableStateOf(""),
) {
    TextField(
        value = inputState.value,
        onValueChange = { inputState.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        placeholder = { Text(text = "输入内容...") },
        singleLine = false,
        maxLines = 3
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    filter: SearchType = SearchType.L2,
    onFilterChange: (SearchType) -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = "Veclite") },
        actions = {
            val expanded = remember { mutableStateOf(false) }
            IconButton(onClick = { expanded.value = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                SearchType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (type == filter),
                                    onClick = { onFilterChange(type) }
                                )
                                Text(
                                    text = type.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }

                        },
                        onClick = {
                            onFilterChange(type)
                            expanded.value = false
                        },
                    )
                }
            }
        }
    )
}

@Composable
fun ListItem(item: ContentItem) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = item.id.toString())
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item.content)
    }
}

@Preview
@Composable
fun MainPreview() {
    VecliteTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MainScreen(
                contentList = listOf(
                    ContentItem(
                        id = 1,
                        content = "test",
                        vec = doubleArrayOf()
                    ),
                    ContentItem(
                        id = 1,
                        content = "test",
                        vec = doubleArrayOf()
                    ),
                    ContentItem(
                        id = 1,
                        content = "test",
                        vec = doubleArrayOf()
                    )
                )
            )
        }
    }
}