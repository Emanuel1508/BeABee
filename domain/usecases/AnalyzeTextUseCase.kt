package com.ibm.internship.beabee.domain.usecases

import com.ibm.internship.beabee.domain.repositories.AnalyzeTextRepository
import com.ibm.internship.beabee.domain.utils.UseCaseResponse

class AnalyzeTextUseCase (private val watsonRepository: AnalyzeTextRepository) {
    suspend operator fun invoke(text: String, maxResults: Int): UseCaseResponse<List<String>> =
        watsonRepository.analyzeText(text, maxResults)
}