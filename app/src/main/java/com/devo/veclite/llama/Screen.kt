package com.devo.veclite.llama

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devo.veclite.entity.Message
import com.devo.veclite.ui.theme.VecliteTheme


@Composable
fun LLamaScreen(
    viewModel: LLamaViewModel
) {

    Scaffold(
        topBar = {
            AppBar(
                isLoad = viewModel.isLoaded.collectAsState().value,
                loadModel = viewModel::loadModel
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ChatMessages(modifier = Modifier.weight(1f), viewModel)
            InputField(viewModel)
        }
    }

}

@Composable
fun InputField(viewModel: LLamaViewModel) {

    val (inputValue, onValueChange) = remember {
        mutableStateOf("")
    }

    val isLoaded by viewModel.isLoaded.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .weight(1f)
            ) {

                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(32.dp)
                        .padding(horizontal = 2.dp)
                        .align(Alignment.Center),
                    value = inputValue,
                    onValueChange = onValueChange,
                    textStyle = TextStyle.Default.copy(
                        textAlign = TextAlign.Justify
                    )
                )
            }

            IconButton(
                onClick = {
                    if (isLoaded) {
                        viewModel.sendMessage(inputValue)
                        onValueChange("")
                    } else {
                        viewModel.tips("请先加载模型")
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(isLoad: Boolean = false, loadModel: () -> Unit = {}) {

    TopAppBar(
        title = { Text(text = "Veclite") },
        actions = {
            val (isExpand, changeExpand) = remember {
                mutableStateOf(false)
            }
            IconButton(onClick = { changeExpand(true) }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = isExpand,
                modifier = Modifier.widthIn(max = 150.dp),
                onDismissRequest = { changeExpand(false) }
            ) {
                DropdownMenuItem(
                    onClick = {
                        loadModel()
                        changeExpand(false)
                    },
                    enabled = !isLoad,
                    text = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "加载模型")
                        }
                    },
                )
            }
        }

    )
}

@Composable
fun ChatMessages(modifier: Modifier, viewModel: LLamaViewModel) {
    val listState = rememberLazyListState()
    val messages by viewModel.messagesFlow.collectAsState()

    Box(modifier = modifier) {
        LazyColumn(
            state = listState
        ) {
            items(messages) { item ->
                MessageItem(item)
            }
        }
    }
}

@Composable
fun MessageItem(msg: Message) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = if (msg.isSelf) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            horizontalAlignment = if (msg.isSelf) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                horizontalArrangement = if (msg.isSelf) Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (msg.isSelf) {
                    Text(
                        text = "我",
                        fontSize = 12.sp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )

                } else {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ChatBot", fontSize = 12.sp)
                }
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .wrapContentWidth(),
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentHeight(),
                    text = msg.content
                )
            }
        }

    }
}

@Preview
@Composable
fun LLamaPreview() {

    VecliteTheme {
        Surface {
            Column {
                InputField(viewModel = LLamaViewModel())
            }
        }
    }

}