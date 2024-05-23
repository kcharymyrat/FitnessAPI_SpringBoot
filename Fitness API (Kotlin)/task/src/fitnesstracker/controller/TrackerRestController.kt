package fitnesstracker.controller

import fitnesstracker.dto.FitnessTrackerRequestDTO
import fitnesstracker.service.FitnessTrackerService
import fitnesstracker.utils.RateLimiter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tracker")
class TrackerRestController @Autowired constructor(
    private val fitnessTrackerService: FitnessTrackerService,
    private val rateLimiter: RateLimiter,
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

        val app = fitnessTrackerService.getAppByApiKey(apiKey)
        if (app == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        println("app.apiKey = ${app.apiKey}, app.category = ${app.category}")

        if (!isRequestGranted(app.category, app.apiKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
        }


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
        println("app.apiKey = ${app.apiKey}, app.name = ${app.name}")

        if (!isRequestGranted(app.category, app.apiKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build()
        }

        val trackerResponseDTOList = fitnessTrackerService.getAllFitnessTrackerResponseDTOs(app.name)
        return ResponseEntity.ok(trackerResponseDTOList)
    }

    private fun isRequestGranted(category: String, apiKey: String): Boolean {
        if (category == "basic") {
            return rateLimiter.isAccessGranted(apiKey)
        }
        return true
    }
}