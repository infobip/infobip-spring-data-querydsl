package com.infobip.spring.data.common

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mapping.PersistentProperty

class PreferredConstructorTest {

    @Test
    internal fun `it should prefer persistence constructors`() {
        val preferredConstructor = PreferredConstructorDiscoverer
                .discover<EntityWithPersistenceConstructor, PersistentProperty<*>>(EntityWithPersistenceConstructor::class.java)

        then(preferredConstructor.parameters).hasSize(3)
        then(preferredConstructor.parameters.map { it.name }).containsExactly("id", "firstName", "lastName")
    }

    @Test
    internal fun `if no persistence constructor, it should take primary constructor`() {
        val preferredConstructor = PreferredConstructorDiscoverer
                .discover<DataClass, PersistentProperty<*>>(DataClass::class.java)

        then(preferredConstructor.parameters).hasSize(3)
        then(preferredConstructor.parameters.map { it.name }).containsExactly("id", "firstName", "lastName")
    }

    @Test
    internal fun `if no primary constructor, it should take constructor with most arguments`() {
        val preferredConstructor = PreferredConstructorDiscoverer
                .discover<EntityWithoutPersistenceConstructor, PersistentProperty<*>>(EntityWithoutPersistenceConstructor::class.java)

        then(preferredConstructor.parameters).hasSize(3)
        then(preferredConstructor.parameters.map { it.name }).containsExactly("id", "firstName", "lastName")
    }
}

data class DataClass(
        @Id private val id: String,
        val firstName: String,
        val lastName: String
)

data class EntityWithPersistenceConstructor(
        @Id private val id: String,
        private val nameInformation: NameInformation
) {
    @PersistenceConstructor
    constructor(id: String, firstName: String, lastName: String): this(id, NameInformation(firstName, lastName))
}

class EntityWithoutPersistenceConstructor {
    val id: String
    val nameInformation: NameInformation

    constructor() {
        this.id = "_"
        this.nameInformation = NameInformation("_", "_")
    }

    constructor(id: String, firstName: String, lastName: String) {
        this.id = id
        this.nameInformation = NameInformation(firstName, lastName)
    }
}

data class NameInformation(val firstName: String, val lastName: String)