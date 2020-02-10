package uk.co.logiccache.web.api

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import uk.co.logiccache.service.PetService
import uk.co.logiccache.web.dto.Pet

@RestController
class PetsApiImpl @Autowired constructor(private val logger: Logger, private val petService: PetService) : PetsApi {

    override fun createPets(pet: Pet): ResponseEntity<Unit> {
        petService.createPet(pet)
        return ResponseEntity(HttpStatus.CREATED)
    }

    override fun listPets(limit: Int?): ResponseEntity<List<Pet>> {
        return ResponseEntity.ok(petService.retrieveAllPets(limit))
    }

    override fun showPetById(petId: Long): ResponseEntity<Pet> {
        val pet = petService.retrievePet(petId)
        return if (pet != null) ResponseEntity.ok(pet) else ResponseEntity.notFound().build()
    }
}