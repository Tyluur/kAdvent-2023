package com.tyluur.day5

import com.tyluur.Puzzle

/**
 * Solver for Day 5 of the Advent of Code challenge, titled "If You Give A Seed A Fertilizer".
 * This object extends the Puzzle class and is responsible for parsing an almanac to determine
 * the lowest location number corresponding to given seed numbers.
 */
object Day5 : Puzzle<Day5.Almanac>(5) {

	/**
	 * Parses the input data to create an Almanac object.
	 * The input is processed to extract seeds and mappings according to the almanac's format.
	 *
	 * @param input A sequence of strings representing the input data.
	 * @return An Almanac object representing the parsed seeds and mappings.
	 */
	override fun parse(input: Sequence<String>): Almanac {
		val lines = input.toList()
		val seeds = lines.first().substringAfter("seeds: ").split(" ").map { it.toLong() }
		val maps = lines.drop(1).associate { section ->
			val header = section.substringBefore("\n")
			val mapName = header.substringBefore("-to-")
			val mappings = section.substringAfter("\n").lines().map { line ->
				val (destStart, sourceStart, length) = line.split(" ").map(String::toLong)
				Mapping(sourceStart, destStart, length)
			}
			mapName to mappings
		}
		return Almanac(seeds, maps)
	}

	/**
	 * Solves Part 1 of the Day 5 puzzle.
	 * Determines the lowest location number corresponding to the initial seed numbers.
	 *
	 * @param input The parsed Almanac object containing seeds and mappings.
	 * @return The lowest location number for the initial seeds as a Long value.
	 */
	override fun solvePart1(input: Almanac): Any {
		return input.seeds.map { seed ->
			convertThroughMaps(seed, input.maps)
		}.minOrNull() ?: Long.MAX_VALUE
	}

	/**
	 * Solves Part 2 of the Day 5 puzzle.
	 * Processes seed ranges and determines the lowest location number for these seed ranges.
	 *
	 * @param input The parsed Almanac object containing seeds and mappings.
	 * @return The lowest location number for the seed ranges as a Long value.
	 */
	override fun solvePart2(input: Almanac): Any {
		val seedRanges = input.seeds.chunked(2).flatMap { (start, length) ->
			(start until (start + length)).toList()
		}
		return seedRanges.map { seed ->
			convertThroughMaps(seed, input.maps)
		}.minOrNull() ?: Long.MAX_VALUE
	}

	/**
	 * Converts a seed number through a series of mappings to find its corresponding location number.
	 *
	 * @param value The initial seed value to be converted.
	 * @param maps The mappings from the almanac to apply to the seed.
	 * @return The final location number after applying all mappings.
	 */
	private fun convertThroughMaps(value: Long, maps: Map<String, List<Mapping>>): Long {
		var result = value
		for ((_, entries) in maps) {
			result = entries.fold(result) { acc, mapping ->
				if (acc in mapping.sourceRange) {
					mapping.destStart + (acc - mapping.sourceStart)
				} else {
					acc
				}
			}
		}
		return result
	}

	/**
	 * Data class representing the almanac with seeds and mappings.
	 *
	 * @property seeds List of initial seed numbers.
	 * @property maps Map of category names to their corresponding mappings.
	 */
	data class Almanac(
		val seeds: List<Long>,
		val maps: Map<String, List<Mapping>>
	)

	/**
	 * Data class representing a mapping entry in the almanac.
	 *
	 * @property sourceStart Starting number of the source range.
	 * @property destStart Starting number of the destination range.
	 * @property length Length of the number range.
	 * @property sourceRange The range of source numbers derived from the sourceStart and length.
	 */
	data class Mapping(
		val sourceStart: Long,
		val destStart: Long,
		val length: Long
	) {
		val sourceRange = sourceStart until sourceStart + length
	}
}
