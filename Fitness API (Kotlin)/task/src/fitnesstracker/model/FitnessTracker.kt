package fitnesstracker.model

import jakarta.persistence.*
import java.time.Instant

@Entity
data class FitnessTracker(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "username")
    var username: String? = null,

    @Column(name = "activity")
    var activity: String? = null,

    @Column(name = "duration")
    var duration: Int? = null,

    @Column(name = "calories")
    var calories: Int? = null,

    var application: String,
    var timestamp: Instant = Instant.now()
)
