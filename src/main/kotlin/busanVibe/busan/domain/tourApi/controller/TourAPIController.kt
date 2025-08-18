package busanVibe.busan.domain.tourApi.controller

import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.tourApi.service.TourCommandService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Tour API 관련 API", description = "이거 지금 호출해도 오류납니다.")
@RestController
@RequestMapping("/api/tour")
class TourAPIController(
    private val tourCommandService: TourCommandService
) {

    @PostMapping("/festivals")
//    @Operation(hidden = true)
    fun saveFestivals(){
        tourCommandService.syncFestivalsFromApi()
    }

    @PostMapping("/place")
    fun savePlace(@RequestParam("place-type") placeType: PlaceType ){
        tourCommandService.getPlace(placeType)
    }


}