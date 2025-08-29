package busanVibe.busan.domain.place.domain

import jakarta.persistence.Embeddable
import lombok.Getter
import java.io.Serializable

@Embeddable
@Getter
open class PlaceLikeId(
    private val userId: Long,
    private val placeId: Long
): Serializable {

}