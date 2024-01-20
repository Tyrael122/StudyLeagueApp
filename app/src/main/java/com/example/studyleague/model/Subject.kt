package com.example.studyleague.model

import androidx.compose.ui.text.style.TextAlign
import com.example.studyleague.ui.components.datagrid.DataGridColumnProperties
import com.example.studyleague.ui.components.datagrid.DataGridRowContent
import com.example.studyleague.ui.components.datagrid.DataGridView

data class Subject(
    val id: Long = 0,
    val name: String = "",
    val workload: Int = 0,
    val studiedHours: Float = 0F,
    val completedQuestionsPercentage: Int = 0,
) : DataGridView {

    override fun toDataGridView(): DataGridRowContent {
        return DataGridRowContent(
            listOf(
                this.name, this.studiedHours.toString(), this.workload.toString()
            )
        )
    }

    companion object {
        val columns: List<DataGridColumnProperties> = listOf(
            DataGridColumnProperties("Nome", 1F, TextAlign.Start),
            DataGridColumnProperties("Horas estudadas", 0.5F, TextAlign.Center),
            DataGridColumnProperties("Carga horária", 0.5F, TextAlign.Center),
        )
    }
}