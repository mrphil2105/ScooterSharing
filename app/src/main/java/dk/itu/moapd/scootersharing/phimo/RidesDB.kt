package dk.itu.moapd.scootersharing.phimo

import android.content.Context
import java.util.*
import kotlin.collections.ArrayList

class RidesDB private constructor(context: Context) {
    private val rides = ArrayList<Scooter>()
    private lateinit var current: Scooter

    companion object : RidesDBHolder<RidesDB, Context>(::RidesDB)

    init {
        rides.add(
            Scooter(" CPH001 ", " ITU ", randomDate())
        )
        rides.add(
            Scooter(" CPH002 ", " Fields ", randomDate())
        )
        rides.add(
            Scooter(" CPH003 ", " Lufthavn ", randomDate())
        )
    }

    fun getRidesList(): List<Scooter> {
        return rides
    }

    fun addScooter(name: String, location: String) {
        val timestamp = randomDate()
        val scooter = Scooter(name, location, timestamp)
        rides.add(scooter)
        current = scooter
    }

    fun updateCurrentScooter(location: String) {
        current.location = location
        current.timestamp = randomDate() // Update timestamp to reflect change
    }

    fun getCurrentScooter(): Scooter {
        return current
    }

    fun getCurrentScooterInfo(): String {
        return current.toString()
    }

    private fun randomDate(): Long {
        val random = Random()
        val now = System.currentTimeMillis()
        val year = random.nextDouble() * 1000 * 60 * 60 * 24 * 365
        return (now - year).toLong()
    }
}

open class RidesDBHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun get(arg: A): T {
        val checkInstance = instance

        if (checkInstance != null) return checkInstance

        return synchronized(this) {
            val checkInstanceAgain = instance

            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
