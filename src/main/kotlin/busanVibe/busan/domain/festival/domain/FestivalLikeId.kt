package busanVibe.busan.domain.festival.domain

import jakarta.persistence.Embeddable
import lombok.Getter
import java.io.Serializable

@Embeddable
@Getter
open class FestivalLikeId(

    private val userId: Long,
    private val festivalId: Long
): Serializable {

}