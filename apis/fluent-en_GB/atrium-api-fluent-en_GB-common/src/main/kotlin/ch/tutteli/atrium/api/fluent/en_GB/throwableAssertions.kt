package ch.tutteli.atrium.api.fluent.en_GB

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.creating.AssertionPlant
import ch.tutteli.atrium.creating.AssertionPlantNullable
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.domain.builders.ExpectImpl
import ch.tutteli.atrium.domain.creating.throwable.thrown.ThrowableThrown

/**
 * Expects that the thrown [Throwable] *is a* [TExpected] (the same type or a sub-type).
 *
 * Notice, that asserting a generic type is [flawed](https://youtrack.jetbrains.com/issue/KT-27826).
 * For instance `isA<MyException<String>>` would only check if the subject is a `MyException` without checking if
 * the element type is actually `String`.
 *
 * @return An assertion container with the new type [TExpected].
 * @throws AssertionError Might throw an [AssertionError] if the assertion made is not correct.
 */
inline fun <reified TExpected : Throwable> ThrowableThrown.Builder.toThrow(): Expect<TExpected> =
    ExpectImpl.throwable.thrown.isA(this, TExpected::class).getExpectOfFeature()

/**
 * Expects that the thrown [Throwable] *is a* [TExpected] (the same type or a sub-type) and
 * that it holds all assertions the given [assertionCreator] creates.
 *
 * Notice, in contrast to other assertion functions which expect an [assertionCreator], this function returns not
 * [Expect] of the initial type, which was `Throwable?` but an [Expect] of the specified type [TExpected].
 * This has the side effect that a subsequent call has only assertion functions available which are suited for [TExpected].
 * Since [Expect] is invariant it especially means that an assertion function which was not written in a generic way
 * will not be available. Fixing such a function is easy (in most cases),
 * you need to transform it into a generic from. Following an example:
 *
 * ```
 * interface Person
 * class Student: Person
 * fun Expect<Person>.foo()        = "dummy"  // limited only to Person, not recommended
 * fun <T: Person> Expect<T>.bar() = "dummy"  // available to Person and all subtypes, the way to go
 * fun Expect<Student>.baz()       = "dummy"  // specific only for Student, ok since closed class
 *
 * val p: Person = Student()
 * expect(p)               // subject of type Person
 *   .isA<Student> { ... } // subject now refined to Student
 *   .baz()                // available via Student
 *   .foo()                // not available to Student, only to Person, results in compilation error
 *   .bar()                // available via T : Person
 * ```
 *
 * Notice, that asserting a generic type is [flawed](https://youtrack.jetbrains.com/issue/KT-27826).
 * For instance `isA<MyException<String>>` would only check if the subject is a `MyException` without checking if
 * the element type is actually `String`.
 *
 * @return An assertion container with the new type [TExpected].
 * @throws AssertionError Might throw an [AssertionError] if the assertion made is not correct.
 */
inline fun <reified TExpected : Throwable> ThrowableThrown.Builder.toThrow(
    noinline assertionCreator: Expect<TExpected>.() -> Unit
): Expect<TExpected> = ExpectImpl.throwable.thrown.isA(this, TExpected::class).addToFeature(assertionCreator)

/**
 * Makes the assertion that no [Throwable] is thrown at all.
 *
 * @throws AssertionError Might throw an [AssertionError] if the assertion made is not correct.
 */
fun ThrowableThrown.Builder.notToThrow(): Expect<Nothing?>
    = ExpectImpl.throwable.thrown.notThrown(this).getExpectOfFeature()

/**
 * Creates an [AssertionPlantNullable] for the [message][Throwable.message] of the plant's
 * [subject][Assert.subject][AssertionPlant.subject] (which is a [Throwable]) and makes the assertion that message [notToBeNull]
 * and uses [assertionCreator] which might create further [Assertion]s which are lazily evaluated at the end.
 *
 * @return Notice, that this assertion function cannot provide a fluent API because it depends on whether the first
 *   assertion [message][Throwable.message] is not null) holds or not.
 * Define subsequent assertions via the [assertionCreator] lambda.
 *
 * @throws AssertionError Might throw an [AssertionError] in case [message][Throwable.message] is `null`
 *   or if an additionally created [Assertion]s (by calling [assertionCreator]) does not hold.
 */
fun <T : Throwable> Expect<T>.message(assertionCreator: Expect<String>.() -> Unit) =
    feature(Throwable::message) { notToBeNull(assertionCreator) }

/**
 * Creates the assertion that the [Throwable]'s [message][Throwable.message] is not null (see [message]) contains
 * [expected]'s [toString] representation and the [toString] representation of the [otherExpected] (if given),
 * using a non disjoint search.
 *
 * It is a shortcut for `message { contains.atLeast(1).values(expected, otherExpected) }`
 *
 * Notice that a runtime check applies which assures that only [CharSequence], [Number] and [Char] are passed
 * (this function expects `Any` for your convenience, so that you can mix [String] and [Int] for instance).
 *
 * @return Notice, that this assertion function cannot provide a fluent API because it depends on whether the first
 *   assertion [message][Throwable.message] is not null) holds or not.
 *
 * @throws AssertionError Might throw an [AssertionError] in case [message][Throwable.message] is `null`
 *   or does not contain [expected] or [otherExpected].
 */
fun <T : Throwable> Expect<T>.messageContains(expected: Any, vararg otherExpected: Any) =
    message { contains(expected, *otherExpected) }
