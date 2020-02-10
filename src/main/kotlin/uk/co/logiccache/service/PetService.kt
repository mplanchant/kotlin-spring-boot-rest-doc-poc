package uk.co.logiccache.service

import org.springframework.stereotype.Service
import uk.co.logiccache.web.dto.Pet

interface PetService {
    fun createPet(pet: Pet)
    fun retrievePet(petId: Long): Pet?
    fun retrieveAllPets(limit: Int?): List<Pet>
}

@Service
class PetServiceImpl : PetService {

    private val fido = Pet(1L, "Fido", "dog", 6)
    private val tiddles = Pet(2L, "Tiddles", "cat", 3)
    private val george = Pet(3L, "George", "tortoise", 26)

    private val pets: MutableList<Pet> = mutableListOf(fido, tiddles, george)

    override fun createPet(pet: Pet) {
        pets.add(pet)
        return
    }

    override fun retrievePet(petId: Long): Pet? {
        return pets.lastOrNull { it.id == petId }
    }

    override fun retrieveAllPets(limit: Int?): List<Pet> {
        return pets.take(limit ?: pets.size)
    }
}
