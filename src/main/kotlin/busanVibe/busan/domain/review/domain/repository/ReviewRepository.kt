package busanVibe.busan.domain.review.domain.repository

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReviewRepository: JpaRepository<Review, Long> {

    @Query(
        """
            SELECT r FROM Review r
            JOIN FETCH r.user
            WHERE r.place = :place
        """
    )
    fun findForDetails(@Param("place")place: Place): List<Review>

}