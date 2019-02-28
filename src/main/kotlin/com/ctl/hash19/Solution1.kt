package com.ctl.hash19

class Solution1 : Solution {

    override fun find(photos: List<Photo>): SlideShow {
        val hPhotos = photos.filterIsInstance(HPhoto::class.java)
        val vPhotos = photos.filterIsInstance(VPhoto::class.java)

        val reverseIndex = hPhotos.flatMap { p -> p.tags.map { it to p } }.groupBy { it.first }.mapValues { it.value.map { it.second } }

        val bigIndex = reverseIndex.filter { it.value.size > 1 }

//        val used = mutableSetOf<HPhoto>()

        val slides = mutableListOf<Slide>()


        val remaining = mutableMapOf<Int, HPhoto>()
        hPhotos.forEach { remaining[it.id] = it }

        var current = hPhotos.first()
        remaining.remove(current.id)
        while (remaining.isNotEmpty()) {
            val candidates = current.tags.flatMap {
                bigIndex[it] ?: listOf()
            }.filter { remaining[it.id] != null }

            slides.add(HSlide(current))
            if (candidates.isNotEmpty()) {
                val bestCandidate = candidates.map { it to Photos.scoreSlides(HSlide(it), HSlide(current)) }.sortedBy { -it.second }.first()
                current = bestCandidate.first
            } else {
                val id = remaining.keys.first()
                current = remaining[id]!!
                remaining.remove(id)
            }
            remaining.remove(current.id)
            println(remaining.size)
        }
        slides.add(HSlide(current))
        return SlideShow(slides)
    }


}