package com.programmersbox.kotlinktslearn

import com.programmersbox.dslannotations.DslClass

sealed class Person(val name: String, var age: Int, private val birthday: (Int) -> Int) {
    fun birthdayDay() {
        age = birthday(age)
    }

    class Adult(name: String, age: Int, birthday: (Int) -> Int) : Person(name, age, birthday)
    class Child(name: String, age: Int, birthday: (Int) -> Int) : Person(name, age, birthday)
}

@DslMarker
annotation class PersonMarker

@DslClass(dslMarker = PersonMarker::class)
class PersonBuilder {
    var name: String = ""
    var age: Int = 0
    var birthday: (Int) -> Int = { it }

    private fun build() = if (age < 21) Person.Child(name, age, birthday) else Person.Adult(name, age, birthday)
    private fun build2() = PersonTwo(name, age, birthday)

    companion object {
        fun builder(block: PersonBuilder.() -> Unit) = PersonBuilder().apply(block).build()
        fun builder2(block: PersonBuilder.() -> Unit) = PersonBuilder().apply(block).build2()
    }
}

enum class PersonLevel { CHILD, ADULT }
data class PersonTwo(val name: String, var age: Int, private val birthday: (Int) -> Int) {
    val type get() = if (age < 21) PersonLevel.CHILD else PersonLevel.ADULT
}