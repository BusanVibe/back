package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceLike
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceLikeRepository: JpaRepository<PlaceLike, Long> {
    fun findAllByPlaceIn(placeList: List<Place>): List<PlaceLike>

}