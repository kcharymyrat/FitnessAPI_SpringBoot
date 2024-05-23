package fitnesstracker.controller

import fitnesstracker.dto.DeveloperSignupDTO
import fitnesstracker.security.UserDetailsImpl
import fitnesstracker.service.DeveloperService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/developers")
class DeveloperRestController @Autowired constructor(
    private val developerService: DeveloperService
) {

    @PostMapping(path=["/signup"])
    fun register(@RequestBody developerSignupDTO: DeveloperSignupDTO): ResponseEntity<Any> {
        println("developerRequestDTO = $developerSignupDTO")
        val developer = developerService.saveDeveloper(developerSignupDTO)

        if (developer == null) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.created(developerService.getDeveloperUrl(developer)).build()
    }

    @GetMapping(path = ["/{id}"])
    fun getDeveloper(
        @PathVariable id: Long, @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<Any> {
        val developerResponseDTO = developerService.getDeveloperResponseDTO(id)
            ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val developerId = developerResponseDTO.id
        if (developerId != userDetails.user.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        return ResponseEntity.ok(developerResponseDTO)
    }
}