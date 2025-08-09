package busanVibe.busan.domain.place.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import org.hibernate.annotations.ColumnDefault

@Entity
class VisitorDistribution(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    @ColumnDefault("0")
    var m1020: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var f1020: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var m3040: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var f3040: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var m5060: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var f5060: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var m70: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("0")
    var f70: Int = 0,

) {

    fun getTotalVisitorCount(): Int {
        return (m1020 ?: 0) +
                (f1020 ?: 0) +
                (m3040 ?: 0) +
                (f3040 ?: 0) +
                (m5060 ?: 0) +
                (f5060 ?: 0) +
                (m70 ?: 0) +
                (f70 ?: 0)
    }


}