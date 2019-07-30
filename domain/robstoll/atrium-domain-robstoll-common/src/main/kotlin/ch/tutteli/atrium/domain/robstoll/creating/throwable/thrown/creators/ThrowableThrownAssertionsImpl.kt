package ch.tutteli.atrium.domain.robstoll.creating.throwable.thrown.creators

import ch.tutteli.atrium.creating.AssertionPlant
import ch.tutteli.atrium.domain.creating.changers.ChangedSubjectPostStep
import ch.tutteli.atrium.domain.creating.throwable.thrown.ThrowableThrown
import ch.tutteli.atrium.domain.creating.throwable.thrown.creators.ThrowableThrownAssertions
import ch.tutteli.atrium.domain.robstoll.lib.creating.throwable.thrown.creators._isThrown
import ch.tutteli.atrium.domain.robstoll.lib.creating.throwable.thrown.creators._nothingThrown
import ch.tutteli.atrium.domain.robstoll.lib.creating.throwable.thrown.creators._toBe
import kotlin.reflect.KClass


class ThrowableThrownAssertionsImpl : ThrowableThrownAssertions {

    override fun <TExpected : Throwable> isA(
        throwableThrownBuilder: ThrowableThrown.Builder,
        expectedType: KClass<TExpected>
    ) = _isThrown(throwableThrownBuilder, expectedType)

    override fun notThrown(
        throwableThrownBuilder: ThrowableThrown.Builder
    ): ChangedSubjectPostStep<Throwable?, Nothing?> = _nothingThrown(throwableThrownBuilder)


    override fun <TExpected : Throwable> toBe(
        throwableThrownBuilder: ThrowableThrown.Builder,
        expectedType: KClass<TExpected>,
        assertionCreator: AssertionPlant<TExpected>.() -> Unit
    ) {
        _toBe(throwableThrownBuilder, expectedType, assertionCreator)
    }

    override fun nothingThrown(throwableThrownBuilder: ThrowableThrown.Builder) {
        _nothingThrown(throwableThrownBuilder).getExpectOfFeature()
    }
}
