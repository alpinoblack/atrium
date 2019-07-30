package ch.tutteli.atrium.specs.integration

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.fluent.en_GB.exactly
import ch.tutteli.atrium.api.fluent.en_GB.values
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.specs.*
import ch.tutteli.atrium.specs.verbs.AssertionVerbFactory
import ch.tutteli.atrium.translations.DescriptionIterableAssertion

abstract class IterableContainsInAnyOrderAtLeast1ValuesAssertionsSpec(
    verbs: AssertionVerbFactory,
    containsInAnyOrderValuesPair: Fun2<Iterable<Double>, Double, Array<out Double>>,
    containsInAnyOrderNullableValuesPair: Fun2<Iterable<Double?>, Double?, Array<out Double?>>,
    rootBulletPoint: String,
    describePrefix: String = "[Atrium] "
) : IterableContainsSpecBase({

    include(object : SubjectLessSpec<Iterable<Double>>(describePrefix,
        containsInAnyOrderValuesPair.first to expectLambda { containsInAnyOrderValuesPair.second(this, 1.2, arrayOf()) }
    ) {})
    include(object : SubjectLessSpec<Iterable<Double?>>(describePrefix,
        "${containsInAnyOrderNullableValuesPair.first} for nullable" to expectLambda { containsInAnyOrderNullableValuesPair.second(this, null, arrayOf()) }
    ) {})

    include(object : CheckingAssertionSpec<Iterable<Double>>(verbs, describePrefix,
        checkingTriple(containsInAnyOrderValuesPair.first, { containsInAnyOrderValuesPair.second(this, 1.2, arrayOf()) }, listOf(1.2).asIterable(), listOf())
    ) {})
    include(object : CheckingAssertionSpec<Iterable<Double?>>(verbs, describePrefix,
        checkingTriple("${containsInAnyOrderNullableValuesPair.first} for nullable", { containsInAnyOrderNullableValuesPair.second(this, 1.2, arrayOf()) }, listOf(1.2 as Double?).asIterable(), listOf())
    ) {})

    val assert: (Iterable<Double>) -> Expect<Iterable<Double>> = verbs::check
    val expect = verbs::checkException

    val (containsInAnyOrderNullableValues, containsInAnyOrderNullableValuesFunArr) = containsInAnyOrderNullableValuesPair
    fun Expect<Iterable<Double?>>.containsInAnyOrderNullableValuesFun(t: Double?, vararg tX: Double?)
        = containsInAnyOrderNullableValuesFunArr(t, tX)

    nonNullableCases(
        describePrefix,
        containsInAnyOrderValuesPair,
        containsInAnyOrderNullableValuesPair
    ) { containsValuesFunArr ->
        fun Expect<Iterable<Double>>.containsFun(t: Double, vararg tX: Double) =
            containsValuesFunArr(t, tX.toTypedArray())


        context("empty collection") {
            val fluentEmptyString = assert(setOf())
            it("1.0 throws AssertionError") {
                expect {
                    fluentEmptyString.containsFun(1.0)
                }.toThrow<AssertionError> {
                    messageContains(
                        "$rootBulletPoint$containsInAnyOrder: $separator",
                        "$anEntryWhichIs: 1.0",
                        "$numberOfOccurrences: 0",
                        "$atLeast: 1"
                    )
                }
            }
        }

        val fluent = assert(oneToSeven)
        context("iterable '$oneToSeven'") {

            context("happy cases") {
                (1..7).forEach {
                    val d = it.toDouble()
                    it("$d does not throw") {
                        fluent.containsFun(d)
                    }
                }
                it("1.0 and 4.0 does not throw") {
                    fluent.containsFun(1.0, 4.0)
                }
                it("1.0 and 1.0 (searching twice in the same assertion) does not throw") {
                    fluent.containsFun(1.0, 1.0)
                }
            }

            context("error cases") {
                it("9.5 throws AssertionError") {
                    expect {
                        fluent.containsFun(9.5)
                    }.toThrow<AssertionError> {
                        messageContains(
                            "$rootBulletPoint$containsInAnyOrder: $separator",
                            "$anEntryWhichIs: 9.5",
                            "$numberOfOccurrences: 0",
                            "$atLeast: 1"
                        )
                    }
                }
                it("9.5 and 7.1 throws AssertionError") {
                    expect {
                        fluent.containsFun(9.5, 7.1)
                    }.toThrow<AssertionError> {
                        message {
                            contains.exactly(2).values(
                                "$numberOfOccurrences: 0",
                                "$atLeast: 1"
                            )
                            contains.exactly(1).values(
                                "$rootBulletPoint$containsInAnyOrder: $separator",
                                "$anEntryWhichIs: 9.5",
                                "$anEntryWhichIs: 7.1"
                            )
                        }
                    }
                }
                it("1.0 and 9.5 throws AssertionError") {
                    expect {
                        fluent.containsFun(1.0, 9.5)
                    }.toThrow<AssertionError> {
                        message {
                            containsRegex("$containsInAnyOrder: $separator.*$anEntryWhichIs: 9.5")
                            containsNot.regex("$containsInAnyOrder: $separator.*$anEntryWhichIs: 1.0")
                        }
                    }
                }

            }
        }
    }

    nullableCases(describePrefix){

        describeFun("$containsInAnyOrderNullableValues for nullable") {

            val list = listOf(null, 1.0, null, 3.0).asIterable()
            val fluent = verbs.check(list)

            context("iterable $list") {
                listOf(
                    1.0 to arrayOf<Double>(),
                    3.0 to arrayOf<Double>(),
                    null to arrayOf<Double>(),
                    null to arrayOf(3.0, null),
                    null to arrayOf(1.0),
                    1.0 to arrayOf(3.0, null)
                ).forEach { (first, rest) ->
                    val restText = if (rest.isEmpty()) "" else ", ${rest.joinToString()}"

                    context("search for $first$restText") {
                        it("$first$restText does not throw") {
                            fluent.containsInAnyOrderNullableValuesFun(first, *rest)
                        }
                    }

                }

                context("search for 2.5") {
                    it("2.5 throws AssertionError") {
                        expect {
                            fluent.containsInAnyOrderNullableValuesFun(2.5)
                        }.toThrow<AssertionError> { messageContains(DescriptionIterableAssertion.CONTAINS.getDefault()) }
                    }
                }
            }
        }
    }
})
