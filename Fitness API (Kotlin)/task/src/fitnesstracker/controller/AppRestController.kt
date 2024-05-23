package fitnesstracker.controller

import fitnesstracker.dto.AppRegisterRequestDTO
import fitnesstracker.security.UserDetailsImpl
import fitnesstracker.service.AppService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/applications")
class AppRestController @Autowired constructor(
    private val appService: AppService
) {

    @PostMapping(path = ["/register"])
    fun registerApplications(
        @RequestBody appRegisterRequestDTO: AppRegisterRequestDTO,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<Any> {
        println("appRegisterDTO = $appRegisterRequestDTO")

        val optionalAppResponseDTP = appService.registerApplications(appRegisterRequestDTO, userDetails.user)

        println("optionalAppResponseDTP = $optionalAppResponseDTP")
        if (optionalAppResponseDTP.isPresent) {
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(optionalAppResponseDTP.get())
        }
        return ResponseEntity.badRequest().build()
    }

}