package fitnesstracker.service

import fitnesstracker.dao.FitnessTrackerRepository
import fitnesstracker.dto.FitnessTrackerRequestDTO
import fitnesstracker.dto.FitnessTrackerResponseDTO
import fitnesstracker.model.FitnessTracker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FitnessTrackerService @Autowired constructor(
    private val fitnessTrackerRepository: FitnessTrackerRepository
) {

    fun getFitnessTrackerById(id: Long): FitnessTracker? {
        return fitnessTrackerRepository.findById(id).orElse(null)
    }

    fun getAllFitnessTrackers(): List<FitnessTracker> {
        return fitnessTrackerRepository.findAllByOrderByIdDesc()
    }

    fun saveFitnessTracker(fitnessTracker: FitnessTracker): FitnessTracker {
        return fitnessTrackerRepository.save(fitnessTracker)
    }

    fun requestDTOToFitnessTracker(requestDTO: FitnessTrackerRequestDTO): FitnessTracker {
        return FitnessTracker(
            username = requestDTO.username,
            activity = requestDTO.activity,
            duration = requestDTO.duration,
            calories = requestDTO.calories
        )
    }

    fun fitnessTrackerToResponseDTO(fitnessTracker: FitnessTracker): FitnessTrackerResponseDTO {
        return FitnessTrackerResponseDTO(
            id = fitnessTracker.id,
            username = fitnessTracker.username,
            activity = fitnessTracker.activity,
            duration = fitnessTracker.duration,
            calories = fitnessTracker.calories
        )
    }

    fun getAllFitnessTrackerResponseDTOs(): List<FitnessTrackerResponseDTO> {
        return getAllFitnessTrackers().map { fitnessTrackerToResponseDTO(it) }
    }
}