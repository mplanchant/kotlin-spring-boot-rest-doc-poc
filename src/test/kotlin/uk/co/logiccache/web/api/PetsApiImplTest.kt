package uk.co.logiccache.web.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.Matchers.`is`
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mockito.`when`
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import uk.co.logiccache.service.PetService
import uk.co.logiccache.web.dto.Pet

@AutoConfigureRestDocs
@WebMvcTest(PetsApi::class)
internal class PetsApiImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var petService: PetService

    @MockBean
    private lateinit var logger: Logger

    private val mapper = jacksonObjectMapper()

    @Test
    fun `create a pet`() {
        val thumper = Pet(4L, "Thumper", "rabbit", 2)

        this.mockMvc.perform(post("/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(thumper)))
                .andExpect(status().isCreated)
                .andDo(document("create-pet-example", requestFields(
                        fieldWithPath("id").description("The id of the pet"),
                        fieldWithPath("name").description("The name of the pet"),
                        fieldWithPath("age").description("The age of the pet"),
                        fieldWithPath("tag").description("The tag of the pet"))))
    }

    @Test
    fun `get a pet`() {
        `when`(petService.retrievePet(1L)).thenReturn(Pet(1L, "Fido", "dog", 6))

        this.mockMvc.perform(get("/v1/pets/{petId}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", `is`("Fido")))
                .andExpect(jsonPath("$.tag", `is`("dog")))
                .andExpect(jsonPath("$.age", `is`(6)))
                .andDo(document("get-a-pet-example", pathParameters(
                        parameterWithName("petId").description("The pet to return")
                )))
    }

    @Test
    fun `get two pets`() {
        `when`(petService.retrieveAllPets(2))
                .thenReturn(listOf(Pet(1L, "Fido", "dog", 6), Pet(2L, "Tiddles", "cat", 3)))

        this.mockMvc.perform(get("/v1/pets")
                .param("limit", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.*", hasSize<Int>(2)))
                .andDo(document("get-pets-example", requestParameters(
                        parameterWithName("limit").description("The number of pets to return")
                )))
    }
}