package busanVibe.busan.domain.festival.repository

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.enums.FestivalStatus
import busanVibe.busan.domain.user.data.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FestivalRepository: JpaRepository<Festival, Long> {


    @Query("""
        SELECT f FROM Festival f
        LEFT JOIN FETCH f.festivalLikes
        LEFT JOIN FETCH f.festivalImages
        WHERE f.id = :id
    """)
    fun findByIdWithLikesAndImages(@Param("id") id: Long): Festival?

    @Query(
        """
            SELECT f FROM Festival f
            LEFT JOIN FETCH f.festivalImages
            LEFT JOIN FETCH f.festivalLikes fl
            LEFT JOIN FETCH fl.user
        """
    )
    fun findAllWithFetch(): List<Festival>

    @Query(
        """
            SELECT f FROM Festival f
            LEFT JOIN FETCH f.festivalImages
            LEFT JOIN FETCH f.festivalLikes fl
            LEFT JOIN FETCH fl.user
            WHERE fl.user = :user
        """
    )
    fun findLikeFestivals(@Param("user") user: User): List<Festival>

    @Query(
        """
            SELECT f FROM Festival f
            RIGHT JOIN FETCH f.festivalImages fi
            WHERE fi.imgUrl LIKE 'https://www.visitbusan.net/uploadImgs/files/cntnts%'
        """
    )
    fun findFestivalImageNotNull(): List<Festival>

}