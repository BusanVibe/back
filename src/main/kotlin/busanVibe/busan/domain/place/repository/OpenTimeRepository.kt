package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.OpenTime
import busanVibe.busan.domain.place.domain.Place
import org.springframework.data.jpa.repository.JpaRepository

interface OpenTimeRepository: JpaRepository<OpenTime, Long> {
    fun findByPlace(place: Place): OpenTime?
}