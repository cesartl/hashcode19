package com.ctl.hash19

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

        return SlideShow(orderSLides(hSlides + vSlides))
    }

    fun orderSLides(slides: List<Slide>): List<Slide> {

        println("number of slides ${slides.size}")

        val reverseIndex = slides.flatMap { p -> p.tags.map { it to p } }.groupBy { it.first }.mapValues { it.value.map { it.second } }

        val bigIndex = reverseIndex.filter { it.value.size > 1 }

        val remaining = mutableMapOf<String, Slide>()
        slides.forEach { remaining[it.id] = it }

        var current = slides.first()
        remaining.remove(current.id)
        val result = mutableListOf<Slide>()
        while (remaining.isNotEmpty()) {
            val candidates = current.tags.asSequence().flatMap {
                bigIndex[it]?.asSequence() ?: sequenceOf()
            }.filter { remaining[it.id] != null }.take(50)

            result.add(current)
            if (candidates.iterator().hasNext()) {
                val bestCandidate = candidates.map { it to Photos.scoreSlides(it, current) }.sortedBy { -it.second }.first()
                current = bestCandidate.first
            } else {
                val id = remaining.keys.first()
                current = remaining[id]!!
                remaining.remove(id)
            }
            remaining.remove(current.id)
            if (remaining.size % 100 == 0) {
            println(remaining.size)
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


        val reverseIndex = vPhotos.flatMap { p -> p.tags.map { it to p } }.groupBy { it.first }.mapValues { it.value.map { it.second } }

        val bigIndex = reverseIndex.filter { it.value.size > 1 }

        val remaining = mutableMapOf<Int, VPhoto>()
        vPhotos.forEach { remaining[it.id] = it }

        var current = vPhotos.first()
        remaining.remove(current.id)

        val slides = mutableListOf<VSlide>()

        val trash = mutableListOf<VPhoto>()

//        while (remaining.isNotEmpty()) {
//            val candidates = current.tags.flatMap {
//                bigIndex[it] ?: listOf()
//            }.filter { remaining[it.id] != null }.map { it to current.tags.intersect(it.tags).size }.sortedBy { -it.second }
//
//            if (candidates.isNotEmpty()) {
//                val best = candidates.first()
//                remaining.remove(best.first.id)
//                val vSlide = VSlide(current, best.first)
//                slides.add(vSlide)
//            } else {
//                trash.add(current)
//            }
//            if (remaining.isNotEmpty()) {
//                current = remaining[remaining.keys.first()]!!
//                remaining.remove(current.id)
//            }
//            if (remaining.size % 10 == 0) {
//                println(remaining.size)
//            }
//        }

        (0 until vPhotos.size / 2).forEach {
            slides.add(VSlide(vPhotos[2 * it], vPhotos[2 * it + 1]))
        }
//

        return slides
    }


}