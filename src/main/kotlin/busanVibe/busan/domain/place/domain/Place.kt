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
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    val region: Region,

    @OneToMany(mappedBy = "place", cascade = [CascadeType.ALL])
    val openTimes: MutableSet<OpenTime> = HashSet()
) : BaseEntity()