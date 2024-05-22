package fitnesstracker.dto

data class FitnessTrackerRequestDTO(
    var username: String? = null,
    var activity: String? = null,
    var duration: Int? = null,
    var calories: Int? = null
)
