package com.ctl.hash19

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.CaffeineSpec

class Solution1 : Solution {

    override fun find(photos: List<Photo>): SlideShow {
        val hPhotos = photos.filterIsInstance(HPhoto::class.java)
        val vPhotos = photos.filterIsInstance(VPhoto::class.java)

        println("hphotos ${hPhotos.size}")
        println("vPhotos ${vPhotos.size}")

        val hSlides = hPhotos.map { HSlide(it) }
        val vSlides = buildVerticalSlides(vPhotos)

        println("hSlides ${hSlides.size}")
        println("vSlides ${vSlides.size}")

        val cache = Caffeine.from(CaffeineSpec.parse("maximumSize=5000000,recordStats")).build<String, Int>()

        return SlideShow(orderSlides(hSlides + vSlides, cache))
    }

    private fun getScore(prev: Slide, next: Slide, cache: Cache<String, Int>): Int {
//        val keys = listOf(prev.id, next.id).sorted().joinToString()
//        return cache.get(keys) {
//            Photos.scoreSlides(prev, next)
//        }!!
        return Photos.scoreSlides(prev, next)
    }


    private fun takeIt(current: Slide, candidate: Slide): Boolean {
        return Math.abs(current.tags.size - candidate.tags.size) / current.tags.size.toDouble() < 0.3
    }

    private fun orderSlides(slides: List<Slide>, cache: Cache<String, Int>): List<Slide> {

        println("number of slides ${slides.size}")

        val reverseIndex = slides.flatMap { p -> p.tags.map { it to p } }.groupBy { it.first }.mapValues { it.value.map { it.second.id } }

        val bigIndex = reverseIndex.filter { it.value.size > 1 }

        val remaining = mutableMapOf<String, Slide>()
        slides.forEach { remaining[it.id] = it }

        var current = slides.first()
        remaining.remove(current.id)
        val result = mutableListOf<Slide>()
        while (remaining.isNotEmpty()) {
            val candidates = current.tags.asSequence().flatMap {
                bigIndex[it]?.asSequence() ?: sequenceOf()
            }.filter { remaining[it] != null }
                    .distinct()
                    .filter { takeIt(current, remaining[it]!!) }
                    .take(5000)

            result.add(current)
            if (candidates.iterator().hasNext()) {
                val bestCandidate = candidates.map { it to getScore(remaining[it]!!, current, cache) }.sortedBy { -it.second }.first()
                current = remaining[bestCandidate.first]!!
            } else {
                val id = remaining.keys.first()
                current = remaining[id]!!
                remaining.remove(id)
            }
            remaining.remove(current.id)
            if (remaining.size % 100 == 0) {
                println(remaining.size)
//                println(cache.stats())
            }
        }
        result.add(current)
        return result
    }

    fun buildVerticalSlides(vPhotos: List<VPhoto>): List<VSlide> {
        if (vPhotos.isEmpty()) {
            return listOf()
        }
        println("vPhotos ${vPhotos.size}")


        val reverseIndex = vPhotos.flatMap { p -> p.tags.map { it to p } }.groupBy { it.first }.mapValues { it.value.map { it.second.id } }

        val bigIndex = reverseIndex.filter { it.value.size > 1 }

        val remaining = mutableMapOf<Int, VPhoto>()
        vPhotos.forEach { remaining[it.id] = it }

        var current = vPhotos.first()
        remaining.remove(current.id)

        val slides = mutableListOf<VSlide>()

        val trash = mutableListOf<VPhoto>()

        while (remaining.isNotEmpty()) {
            val candidates = current.tags.asSequence().flatMap {
                bigIndex[it]?.asSequence() ?: sequenceOf()
            }.filter { remaining[it] != null }
                    .distinct()
                    .take(1500)
                    .map { it to current.tags.intersect(remaining[it]!!.tags).size }.sortedBy { it.second }

            if (candidates.iterator().hasNext()) {
                val best = candidates.first()
                val vSlide = VSlide(current, remaining[best.first]!!)
                remaining.remove(best.first)
                slides.add(vSlide)
            } else {
                trash.add(current)
            }
            if (remaining.isNotEmpty()) {
                current = remaining[remaining.keys.first()]!!
                remaining.remove(current.id)
            }
            if (remaining.size % 10 == 0) {
                println(remaining.size)
            }
        }

//        (0 until vPhotos.size / 2).forEach {
//            slides.add(VSlide(vPhotos[2 * it], vPhotos[2 * it + 1]))
//        }
////

        return slides
    }


}