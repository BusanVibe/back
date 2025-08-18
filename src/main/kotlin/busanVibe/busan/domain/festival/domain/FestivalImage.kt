package busanVibe.busan.domain.festival.domain

import busanVibe.busan.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class FestivalImage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 255)
    val imgUrl : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    var festival: Festival

): BaseEntity() {

}
