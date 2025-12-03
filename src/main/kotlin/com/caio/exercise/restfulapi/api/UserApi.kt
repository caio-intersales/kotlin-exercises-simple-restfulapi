package com.caio.exercise.restfulapi.api

import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.DELETE

import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.core.MediaType

import io.smallrye.mutiny.Uni

import com.caio.exercise.restfulapi.model.User
import com.caio.exercise.restfulapi.model.UserPatch
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.core.Response

/**
 * Endpoints for managing users
 */

@Path("/users")
class UserResource {

    /**
     * ENDPOINT: Read all users
     * This GET endpoint will return all users stored in the database
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    fun listUsers(): Uni<List<User>> {
        // Panache static method to find all users
        return User.listAll()
    }

    /**
     * ENDPOINT: Check data from a specific user using their ID
     * This GET endpoint will return all data from a specific user, providing their ID
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun checkSpecificUser(@PathParam("id") userId: Long): Uni<User?> {
        return User.findById(userId)
    }

    /**
     * ENDPOINT: Add a random user (for testing purposes)
     * This GET endpoint will generate a random user, giving them a first and last name and an email, based on their full name
     */
    @GET
    @Path("/new_random")
    fun createARandomUser(): Uni<User> {
        // Define possible names
        val possibleFirstNames  = listOf("James", "Martha", "Maria", "John", "Karen")
        val possibleLastNames   = listOf("Mueller", "Cameron", "Sparrow", "Miles")

        // Creates names
        val newFirstName        = possibleFirstNames.random()
        val newLastName         = possibleLastNames.random()

        // Create email & transform names
        val newEmailAddress     = "${newFirstName.lowercase()}.${newLastName.lowercase()}@email.com"

        // Define new user
        val newUser = User(
            firstName   = newFirstName,
            lastName    = newLastName,
            email       = newEmailAddress
        )

        // Adds new user and returns their data
        return User.persist(newUser).map { newUser }
    }

    /**
     * ENDPOINT: Add a new user through POST request
     * This POST endpoint will add a new user to the database with the data sent through a POST request
     */
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    fun createNewUser(newUser: User): Uni<User> {
        println("Received data: ${newUser.firstName} ${newUser.lastName}; ${newUser.email}")

        val newUser = User(
            firstName = newUser.firstName,
            lastName = newUser.lastName,
            email = newUser.email
        )

        return User.persist(newUser).map{ newUser }
    }

    // Endpoint: delete specific user (DELETE)
    /**
     * ENDPOINT: Delete a user based on their ID
     * This DELETE endpoint will delete a user based on a provided ID through a DELETE request
     */
    @DELETE
    @Path("/delete/{id}")
    @WithTransaction
    fun deleteUser(@PathParam("id") userId: Long): Uni<Boolean> {
        return User.deleteById(userId)
    }

    /**
     * ENDPOINT: Update a user based on their ID
     * This PATCH endpoint will update the data from a user, based on the provided ID, checking whether all data has been sent or not and updating only the data that has actually been sent
     */
    @PATCH
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @WithTransaction
    fun updateUser(@PathParam("id") userId: Long, patch: UserPatch): Uni<Response> {
        // Find the user in the db
        return User.findById(userId)
            .onItem().ifNotNull().transform{ existingUser ->
                // Check which fields were actually sent and store them to be saved
                if(patch.firstName != null){
                    existingUser?.firstName = patch.firstName
                }
                if(patch.lastName != null){
                    existingUser?.lastName = patch.lastName
                }
                if(patch.email != null){
                    existingUser?.email = patch.email
                }

                // Changes are commited here by Panache
                Response.ok(existingUser).build()
            }
            // Throws error if the user is not found
            .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build())
    }
}
