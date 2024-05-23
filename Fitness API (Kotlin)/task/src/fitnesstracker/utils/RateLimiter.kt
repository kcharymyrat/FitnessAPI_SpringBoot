package fitnesstracker.utils

import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@EnableAsync
class RateLimiter {
    private var tokensAvailable = ConcurrentHashMap<String, Int>()


    fun isAccessGranted(appApiKey: String): Boolean {
        if (tokensAvailable.containsKey(appApiKey)) {
            if (tokensAvailable[appApiKey]!! > 0) {
                tokensAvailable[appApiKey] = tokensAvailable[appApiKey]!! - 1
                return true
            }
        } else {
            tokensAvailable[appApiKey] = 0
            return true
        }
        return false
    }

    @Async
    @Scheduled(fixedRate = 1000)
    fun replenishTokens() {
        tokensAvailable.forEach { (key, value) ->
            if (value < 1) {
                tokensAvailable[key] = 1
            }
        }
    }
}