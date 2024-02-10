package br.studyleague.ui.components.datagrid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.studyleague.ui.components.DefaultOutlinedTextField


@Composable
fun <T : DataGridView> DataGrid(
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit = {},
    backgroundColor: Color = Color(0xFFEEEEEE),
    columns: List<DataGridColumnProperties>,
    items: List<T>,
    selectedItems: List<T> = emptyList(),
    onSelectionChanged: (List<T>) -> Unit = {},
    isDatagridItemSelectable: Boolean = false,
    isSearchBarVisible: Boolean = true,
    noContentText: String = "Sem registros.",
) {
    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(10.dp))
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
    ) {
        val selectionViewModel: SelectionViewModel<T> = viewModel(
            factory = SelectionViewModel.Factory(
                items, selectedItems, onSelectionChanged
            )
        )

        val uiState by selectionViewModel.uiState.collectAsState()

        DataGridHeader(columns = columns,
            isSearchBarVisible = isSearchBarVisible,
            isAllItemsSelected = uiState.isAllItemsSelected,
            isDataGridItemSelectable = isDatagridItemSelectable,
            selectAllItems = { selectionViewModel.selectAllItems() })

        LazyItemGrid(items = items.map { it.toDataGridView() },
            columns = columns,
            onItemClick = { onItemClick(items[it]) },
            noContentText = noContentText,
            isDataGridItemSelectable = isDatagridItemSelectable,
            onSelectItem = { selectionViewModel.toggleItemSelection(items[it]) },
            isItemSelected = { uiState.selectedItems.contains(items[it]) },
            modifier = Modifier.weight(1F)
                .background(Color.White)
        )
    }
}

@Composable
private fun DataGridHeader(
    modifier: Modifier = Modifier,
    columns: List<DataGridColumnProperties>,
    selectAllItems: () -> Unit,
    isAllItemsSelected: Boolean,
    isDataGridItemSelectable: Boolean,
    isSearchBarVisible: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        if (isSearchBarVisible) {
            SearchBar(modifier = Modifier.padding(top = 10.dp))
        }

        DataGridColumnHeader(
            modifier = Modifier.heightIn(min = 45.dp),
            selectAllItems = selectAllItems,
            isAllItemsSelected = isAllItemsSelected,
            isDataGridItemSelectable = isDataGridItemSelectable,
            columns = columns,
        )
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
    ) {
        FilterButton()

        Spacer(Modifier.width(16.dp))

        SearchTextField(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DataGridColumnHeader(
    modifier: Modifier = Modifier,
    selectAllItems: () -> Unit,
    columns: List<DataGridColumnProperties>,
    isDataGridItemSelectable: Boolean,
    isAllItemsSelected: Boolean,
) {
    DataGridRow(
        columnContentMap = columns.associateWith { it.name },
        style = TextStyle(), // DataGridTypograhpy.bodyMedium,
        textColor = MaterialTheme.colorScheme.onSurface,
        isSelectable = isDataGridItemSelectable,
        onSelect = selectAllItems,
        isItemSelected = isAllItemsSelected,
        modifier = modifier,
    )
}

@Composable
private fun LazyItemGrid(
    modifier: Modifier = Modifier,
    noContentText: String,
    items: List<DataGridRowContent>,
    columns: List<DataGridColumnProperties>,
    onItemClick: (Int) -> Unit,
    onSelectItem: (Int) -> Unit,
    isItemSelected: (Int) -> Boolean,
    isDataGridItemSelectable: Boolean,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {

        if (items.isEmpty()) {
            item {
                NoContentIndicator(
                    text = noContentText,
                    modifier = Modifier.fillParentMaxSize()
                )
            }
        }

        items(items.size) { index ->
            val dataGridRowContent = items[index]
            val columnContentMap = generateColumnContentMap(columns, dataGridRowContent)

            DataGridRow(columnContentMap = columnContentMap,
                isSelectable = isDataGridItemSelectable,
                onSelect = { onSelectItem(index) },
                isItemSelected = isItemSelected(index),
                modifier = Modifier
                    .heightIn(min = 60.dp)
                    .clickable { onItemClick(index) }
                    .padding(horizontal = 12.dp))
        }
    }
}

@Composable
private fun DataGridRow(
    modifier: Modifier = Modifier,
    columnContentMap: Map<DataGridColumnProperties, String>,
    style: TextStyle = TextStyle(), // DataGridTypograhpy.bodyLarge,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    isSelectable: Boolean,
    isItemSelected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isSelectable) {
            Checkbox(
                checked = isItemSelected,
                onCheckedChange = { onSelect() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(end = 10.dp)
            )
        }

        columnContentMap.forEach {
            val text = it.value
            val dataGridColumn = it.key

            Text(
                text = text,
                textAlign = dataGridColumn.textAlign,
                style = style,
                color = textColor,
                modifier = Modifier
                    .weight(dataGridColumn.weight)
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun FilterButton(modifier: Modifier = Modifier) {
    ElevatedButton(
        onClick = { /*TODO*/ },
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .width(50.dp)
            .fillMaxHeight()
    ) {
        Icon(Icons.Filled.FilterAlt, contentDescription = null)
    }
}

@Composable
fun SearchTextField(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxHeight()
    ) {

        var text by remember { mutableStateOf("") }

        DefaultOutlinedTextField(value = text,
            onValueChange = {
                text = it
            },
            textStyle = TextStyle(fontWeight = FontWeight.Normal),
            placeholder = { Text("Pesquisar...") },
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp),
            unfocusedBorderThickness = 0.dp,
            focusedBorderThickness = 0.dp,
            modifier = Modifier
                .weight(1f)
                .shadow(1.dp, RoundedCornerShape(10.dp))
                .background(Color.White)
        )

    }
}

private fun generateColumnContentMap(
    columns: List<DataGridColumnProperties>, dataGridRowContent: DataGridRowContent
): Map<DataGridColumnProperties, String> {

    var columnContentMap = mapOf<DataGridColumnProperties, String>()
    for (i in columns.indices) {

        var text = ""
        if (i < dataGridRowContent.texts.size) {
            text = dataGridRowContent.texts[i]
        }

        columnContentMap = columnContentMap.plus(Pair(columns[i], text))
    }

    return columnContentMap
}

interface DataGridView {
    fun toDataGridView(): DataGridRowContent
}

@Composable
fun NoContentIndicator(
    modifier: Modifier = Modifier,
    text: String,
) {
    IconIndicator(
        modifier = modifier,
        text = text,
        icon = Icons.Outlined.FolderOff,
    )
}

@Composable
fun IconIndicator(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp
    ),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth(.25F)
                .aspectRatio(1F)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = text,
            style = textStyle,
        )
    }
}

data class DataGridColumnProperties(
    val name: String, val weight: Float, val textAlign: TextAlign = TextAlign.Start
)

data class DataGridRowContent(val texts: List<String>)