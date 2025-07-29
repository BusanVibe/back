package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.place.enums.PlaceType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.math.BigDecimal

@Entity
class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 10)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: PlaceType,

    @Column(nullable = false)
    val latitude: BigDecimal,

    @Column(nullable = false)
    val longitude: BigDecimal,

    @Column(nullable = false, length = 50)
    val address: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val introduction: String,

    @Column(nullable = false, length = 20)
    val phone: String,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "region_id", nullable = false)
//    val region: Region,
//
//    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
//    val reviews: List<Review> = emptyList(),

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    val placeLikes: Set<PlaceLike>,

    @OneToOne(mappedBy = "place", fetch = FetchType.LAZY)
    val openTime: OpenTime,

    @OneToMany(mappedBy="place", fetch = FetchType.LAZY)
    val placeImages: Set<PlaceImage>

) : BaseEntity()