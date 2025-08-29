package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.user.data.User
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
open class PlaceLike(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long?,

    @EmbeddedId
    val id: PlaceLikeId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @MapsId("placeId")
    val place: Place,

): BaseEntity() {

}