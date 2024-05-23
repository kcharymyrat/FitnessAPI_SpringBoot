package fitnesstracker.controller

import fitnesstracker.dto.FitnessTrackerRequestDTO
import fitnesstracker.security.UserDetailsImpl
import fitnesstracker.service.FitnessTrackerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tracker")
class TrackerRestController @Autowired constructor(
    private val fitnessTrackerService: FitnessTrackerService
) {

    @PostMapping(path=[""])
    fun createTracker(
        @RequestBody trackerRequestDTO: FitnessTrackerRequestDTO,
        @RequestHeader("X-API-Key") apiKey: String?,
    ): ResponseEntity<Any> {
        println("trackerRequestDTO = $trackerRequestDTO")
        println("apiKey = $apiKey")

        val isApiKeyValid = fitnessTrackerService.validateApiKey(apiKey)
        if (!isApiKeyValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        println("apiKey = $apiKey is present")

        val tracker = fitnessTrackerService.requestDTOToFitnessTracker(trackerRequestDTO)
        val savedTracker = fitnessTrackerService.saveFitnessTracker(tracker)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTracker)
    }

    @GetMapping(path = [""])
    fun getAllTrackers(@RequestHeader("X-API-Key") apiKey: String?): ResponseEntity<Any> {
        println("apiKey = $apiKey")

        val isApiKeyValid = fitnessTrackerService.validateApiKey(apiKey)
        if (!isApiKeyValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        println("apiKey = $apiKey is present")

        val app = fitnessTrackerService.getAppByApiKey(apiKey)
        if (app == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        println("app.name = ${app.name}")

        val trackerResponseDTOList = fitnessTrackerService.getAllFitnessTrackerResponseDTOs(app.name)
        return ResponseEntity.ok(trackerResponseDTOList)
    }
}