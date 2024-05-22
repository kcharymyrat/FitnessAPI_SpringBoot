package fitnesstracker.dao

import fitnesstracker.model.FitnessTracker
import org.springframework.data.jpa.repository.JpaRepository


interface FitnessTrackerRepository: JpaRepository<FitnessTracker, Long> {
    fun findAllByOrderByIdDesc(): List<FitnessTracker>
}