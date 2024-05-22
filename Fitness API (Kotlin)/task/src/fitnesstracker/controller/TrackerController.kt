package fitnesstracker.controller

import fitnesstracker.dto.FitnessTrackerRequestDTO
import fitnesstracker.dto.FitnessTrackerResponseDTO
import fitnesstracker.model.FitnessTracker
import fitnesstracker.service.FitnessTrackerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TrackerController @Autowired constructor(
    private val fitnessTrackerService: FitnessTrackerService
) {

    @PostMapping(path=["/api/tracker"])
    fun createTracker(@RequestBody trackerRequestDTO: FitnessTrackerRequestDTO): ResponseEntity<Any> {
        println("trackerRequestDTO = $trackerRequestDTO")
        val tracker = fitnessTrackerService.requestDTOToFitnessTracker(trackerRequestDTO)
        val savedTracker = fitnessTrackerService.saveFitnessTracker(tracker)
        println("savedTracker = $savedTracker")
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTracker)
    }

    @GetMapping(path = ["/api/tracker"])
    fun getAllTrackers(): ResponseEntity<Any> {
        val trackerResponseDTOList = fitnessTrackerService.getAllFitnessTrackerResponseDTOs()
        return ResponseEntity.ok(trackerResponseDTOList)
    }
}