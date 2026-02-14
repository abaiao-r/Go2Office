package com.example.go2office.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service to fetch public holidays from free APIs.
 * Uses Nager.Date API - 100% free, no API key needed.
 *
 * API: https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}
 * Supported: 100+ countries
 * Rate limit: None
 * Cost: FREE!
 */
@Singleton
class HolidayApiService @Inject constructor() {

    private companion object {
        const val BASE_URL = "https://date.nager.at/api/v3"
        const val TIMEOUT_MS = 10_000
    }

    /**
     * Fetch public holidays for a country and year.
     *
     * @param countryCode ISO 3166-1 alpha-2 (PT, ES, BR, etc)
     * @param year Year (2024, 2025, 2026...)
     * @return List of holidays or empty if error
     */
    suspend fun fetchPublicHolidays(countryCode: String, year: Int): Result<List<HolidayDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/PublicHolidays/$year/$countryCode")
                val connection = url.openConnection() as HttpURLConnection

                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("Accept", "application/json")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val holidays = parseHolidays(response)
                    Result.success(holidays)
                } else {
                    Result.failure(Exception("HTTP $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get list of available countries from API.
     * Endpoint: /AvailableCountries
     */
    suspend fun fetchAvailableCountries(): Result<List<CountryDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/AvailableCountries")
                val connection = url.openConnection() as HttpURLConnection

                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("Accept", "application/json")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val countries = parseCountries(response)
                    Result.success(countries)
                } else {
                    Result.failure(Exception("HTTP $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parseHolidays(json: String): List<HolidayDto> {
        val holidays = mutableListOf<HolidayDto>()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            holidays.add(
                HolidayDto(
                    date = obj.getString("date"),
                    localName = obj.getString("localName"),
                    name = obj.getString("name"),
                    countryCode = obj.getString("countryCode"),
                    global = obj.optBoolean("global", true),
                    counties = obj.optJSONArray("counties")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    } ?: emptyList()
                )
            )
        }

        return holidays
    }

    private fun parseCountries(json: String): List<CountryDto> {
        val countries = mutableListOf<CountryDto>()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            countries.add(
                CountryDto(
                    countryCode = obj.getString("countryCode"),
                    name = obj.getString("name")
                )
            )
        }

        return countries.sortedBy { it.name }
    }
}

/**
 * Holiday DTO from API response.
 */
data class HolidayDto(
    val date: String,           // "2026-01-01"
    val localName: String,      // "Ano Novo" (in local language)
    val name: String,           // "New Year's Day" (in English)
    val countryCode: String,    // "PT"
    val global: Boolean,        // true if nationwide, false if regional
    val counties: List<String>  // List of regions if not global
)

/**
 * Country DTO from API response.
 */
data class CountryDto(
    val countryCode: String,    // "PT"
    val name: String            // "Portugal"
)

