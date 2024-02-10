package br.studyleague.ui.components.datagrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SelectionViewModel<T>(
    initialSelectedItems: List<T>,
    items: List<T>,
    private val onSelectionChanged: (List<T>) -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataGridUiState(items = items))
    val uiState = _uiState.asStateFlow()

    private val fetchedItems: List<T>
        get() {
            return _uiState.value.items
        }

    private val selectedItems: List<T>
        get() {
            return _uiState.value.selectedItems
        }

    init {
        updateSelectionUiState(initialSelectedItems)
    }

    fun toggleItemSelection(item: T) {
        updateSelectionUiState(
            if (selectedItems.contains(item)) {
                selectedItems.minus(item)
            } else {
                selectedItems.plus(item)
            }
        )
    }

    fun selectAllItems() {
        updateSelectionUiState(
            if (isAllItemsSelected()) {
                emptyList()
            } else {
                fetchedItems
            }
        )
    }

    private fun updateSelectionUiState(newSelectedItems: List<T>) {
        _uiState.update {
            it.copy(selectedItems = newSelectedItems)
        }

        _uiState.update {
            it.copy(isAllItemsSelected = isAllItemsSelected())
        }

        onSelectionChanged(newSelectedItems)
    }

    private fun isAllItemsSelected(): Boolean {
        if (fetchedItems.isEmpty()) {
            return false
        }

        return selectedItems.size == fetchedItems.size
    }

    companion object {
        fun <T> Factory(
            initialSelectedItems: List<T>,
            items: List<T>,
            onSelectionChanged: (List<T>) -> Unit
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SelectionViewModel(initialSelectedItems, items, onSelectionChanged)
                }
            }
        }
    }
}

data class DataGridUiState<T>(
    val selectedItems: List<T> = emptyList(),
    val isAllItemsSelected: Boolean = false,
    val items: List<T> = emptyList(),
)
