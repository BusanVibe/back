package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.review.domain.Review
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.math.BigDecimal

@Entity
class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val contentId: Long,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: PlaceType,

    @Column(nullable = false)
    val latitude: BigDecimal? = null,

    @Column(nullable = false)
    val longitude: BigDecimal? = null,

    @Column(nullable = false, length = 50)
    val address: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val introduction: String,

    @Column(nullable = false, length = 50)
    val phone: String,

    // -----

    @Column(nullable = false)
    val useTime: String,

    @Column(nullable = false)
    val restDate: String,


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "region_id", nullable = false)
//    val region: Region,
//
    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    val reviews: List<Review>,

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    val placeLikes: Set<PlaceLike>,

    @OneToOne(mappedBy = "place", fetch = FetchType.LAZY, optional = true)
    val openTime: OpenTime? = null,

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val placeImages: MutableSet<PlaceImage> = mutableSetOf(),

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "visitor_distribution_id")
    val visitorDistribution: VisitorDistribution,

) : BaseEntity(){

    fun addImage(imgUrl: String) {
        val image = PlaceImage(imgUrl = imgUrl, place = this)
        placeImages.add(image)
    }

    fun removeImage(image: PlaceImage) {
        placeImages.remove(image)
    }

}