package com.caio.exercise.restfulapi.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import kotlinx.serialization.Serializable

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion

/**
 * Data class managed by Panache
 */

@Entity
@Table(name="users")
@Serializable
data class User(
    // Primary key (id) explicitly declared for Panache
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var firstName: String? = "",
    var lastName: String? = "",
    var email: String? = ""
) : PanacheEntityBase{
    companion object: PanacheCompanion<User>
}

/**
 * Data class for updating user
 */

@Serializable
data class UserPatch(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null
)