package com.ibm.internship.beabee.domain.repositories

import com.ibm.internship.beabee.domain.utils.UseCaseResponse

interface AnalyzeTextRepository {
    suspend fun analyzeText(text: String, maxResults: Int): UseCaseResponse<List<String>>
}