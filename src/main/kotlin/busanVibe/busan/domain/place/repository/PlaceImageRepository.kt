package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceImage
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceImageRepository: JpaRepository<PlaceImage, Long> {

    fun findByPlaceIn(placeList: List<Place>): List<PlaceImage>

}