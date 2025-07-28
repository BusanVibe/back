package busanVibe.busan.domain.place.dto

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.review.domain.Review

class PlaceQueryResult (
    val place: Place,
    val placeLikes: List<PlaceLike>,
    val reviews: List<Review>
){



}