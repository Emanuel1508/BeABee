package com.ibm.beabee.data.repositories

import com.ibm.beabee.data.BuildConfig
import com.ibm.beabee.data.utils.Constants
import com.ibm.beabee.utils.BeeLog
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import com.ibm.internship.beabee.domain.repositories.AnalyzeTextRepository
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesOptions
import com.ibm.watson.natural_language_understanding.v1.model.Features
import com.ibm.watson.natural_language_understanding.v1.model.KeywordsOptions
import javax.inject.Inject
import com.ibm.cloud.sdk.core.service.exception.BadRequestException
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse

class AnalyzeTextRepositoryImpl @Inject constructor() : AnalyzeTextRepository {
    private val TAG = this::class.java.simpleName

    override suspend fun analyzeText(text: String, maxResults: Int): UseCaseResponse<List<String>> {
        val authenticator = buildAuthenticator()
        val naturalLanguageUnderstanding = buildNaturalLanguageUnderstanding(authenticator)
        return try {
            processText(text, naturalLanguageUnderstanding, maxResults)
        } catch (exception: BadRequestException) {
            getException(exception, ErrorMessage.UNSUPPORTED_LANGUAGE)
        } catch (exception: ServiceResponseException) {
            getException(exception, ErrorMessage.NOT_ENOUGH_TEXT)
        } catch (exception: Exception) {
            getException(exception, ErrorMessage.GENERAL)
        }
    }

    private fun buildAuthenticator(): IamAuthenticator =
        IamAuthenticator.Builder()
            .apikey(BuildConfig.NLP_API_KEY)
            .build()

    private fun buildNaturalLanguageUnderstanding(authenticator: IamAuthenticator): NaturalLanguageUnderstanding {
        val naturalLanguageUnderstanding =
            NaturalLanguageUnderstanding(BuildConfig.NLP_API_VERSION, authenticator)
        naturalLanguageUnderstanding.serviceUrl = BuildConfig.NLP_SERVER_URL
        return naturalLanguageUnderstanding
    }

    private fun buildCategories(): CategoriesOptions =
        CategoriesOptions.Builder()
            .limit(Constants.DEFAULT_LIMIT_RESULTS)
            .build()

    private fun buildKeywords(
        emotion: Boolean = true,
        sentiment: Boolean = true
    ): KeywordsOptions =
        KeywordsOptions.Builder()
            .emotion(emotion)
            .sentiment(sentiment)
            .build()

    private fun buildFeatures(): Features =
        Features.Builder()
            .keywords(buildKeywords())
            .categories(buildCategories())
            .build()

    private fun buildParameters(text: String): AnalyzeOptions =
        AnalyzeOptions.Builder()
            .text(text)
            .features(buildFeatures())
            .build()

    private fun getAnalysisResults(
        text: String,
        naturalLanguageUnderstanding: NaturalLanguageUnderstanding
    ): AnalysisResults = naturalLanguageUnderstanding
        .analyze(buildParameters(text))
        .execute()
        .result

    private fun filterResults(results: AnalysisResults, maxResults: Int): List<String> =
        results.categories
            .asSequence()
            .filter { category -> category.score > Constants.MIN_SCORE }
            .map { category ->
                category.label
                    .removePrefix(Constants.RESULT_DELIMITER)
                    .split(Constants.RESULT_DELIMITER)
            }
            .flatten()
            .toSet()
            .take(maxResults)
            .toList()

    private fun processText(
        text: String,
        naturalLanguageUnderstanding: NaturalLanguageUnderstanding,
        maxResults: Int
    ): UseCaseResponse<List<String>> {
        val response = getAnalysisResults(text, naturalLanguageUnderstanding)
        BeeLog.d(TAG, "Watson response: $response")
        val listResponses = filterResults(response, maxResults)
        listResponses.ifEmpty {
            return UseCaseResponse.Success(listOf(Constants.DEFAULT_TAG))
        }
        BeeLog.d(TAG, "List filtered results: $listResponses")
        return UseCaseResponse.Success(listResponses)
    }

    private fun getException(
        exception: Exception,
        errorMessage: ErrorMessage
    ): UseCaseResponse.Failure {
        BeeLog.e(TAG, "Watson failed with exception: $exception")
        return UseCaseResponse.Failure(errorMessage)
    }
}