package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.place.enums.PlaceType
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

    @Column(nullable = false, scale = 5)
    val latitude: BigDecimal? = null,

    @Column(nullable = false, scale = 5)
    val longitude: BigDecimal? = null,

    @Column(nullable = false, length = 50)
    val address: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val introduction: String,

    @Column(nullable = false, length = 50)
    val phone: String,

    @Column(nullable = false)
    val useTime: String,

    @Column(nullable = false)
    val restDate: String,

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    val placeLikes: Set<PlaceLike>,

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