package br.studyleague.model

import dtos.SubjectDTO

data class StudyCycleEntry(val id: Long, val subject: SubjectDTO, val duration: Int, val isSelected: Boolean = false, val isExpanded: Boolean = false)