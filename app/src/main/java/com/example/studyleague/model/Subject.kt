package com.example.studyleague.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import br.studyleague.dtos.SubjectDTO
import com.example.studyleague.ui.components.datagrid.DataGridColumnProperties
import com.example.studyleague.ui.components.datagrid.DataGridRowContent
import com.example.studyleague.ui.components.datagrid.DataGridView
import com.example.studyleague.ui.components.randomReadableColor

data class Subject(
    val subjectDTO: SubjectDTO = SubjectDTO(),
//    val name: String by subjectDTO::name,
    val color: Color = Color.randomReadableColor(),
) : DataGridView {

    override fun toDataGridView(): DataGridRowContent {
        return DataGridRowContent(
            listOf(
                this.subjectDTO.name,
                this.subjectDTO.allTimeStatistic.hours.toString(),
                "0" // TODO: Workload here.
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