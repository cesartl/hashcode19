package com.ctl.hash19

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MainTest {


    val aInput = Photos.parse(InputUtils.getLines("a_example.txt"))
    val bInput = Photos.parse(InputUtils.getLines("b_lovely_landscapes.txt"))
    val cInput = Photos.parse(InputUtils.getLines("c_memorable_moments.txt"))
    val dInput = Photos.parse(InputUtils.getLines("d_pet_pictures.txt"))
    val eInput = Photos.parse(InputUtils.getLines("e_shiny_selfies.txt"))

    fun makeSlide(name: String, photos: List<Photo>, solution: Solution): SlideShow {
        val slide = solution.find(photos)
        println("score is " + slide.score())
        println(slide.slides.size)
        slide.write("$name-out.txt")
        return slide
    }

    fun allTags(photos: List<Photo>): Set<String> {
        return photos.flatMap { it.tags }.toSet()
    }

    fun countTags(name: String, photos: List<Photo>) {
        val tags = allTags(photos)
        println("$name has ${tags.size} tags")
    }

    fun tagStat(photos: List<Photo>): Map<String, Int> {
        return photos.flatMap { p -> p.tags.map { p.id to it } }.groupingBy { it.second }.eachCount()
    }

    fun tagStat(name: String, photos: List<Photo>) {
        println("$name")
        val hPhotos = photos.filterIsInstance(HPhoto::class.java)
        val vPhotos = photos.filterIsInstance(VPhoto::class.java)


        println("vPhotos ${vPhotos.size}")
        println("hPhotos ${hPhotos.size}")

        val count = tagStat(photos)
        val sorted = count.toList().sortedBy { -it.second }

        val with2 = sorted.filter { it.second == 2 }
        val with1 = sorted.filter { it.second == 1 }

        println("with2 ${with2.size}")
        println("with1 ${with1.size}")


        println(sorted.take(100))
    }

    @Test
    internal fun testAllTags() {
//        countTags("a", aInput)
//        countTags("b", bInput)
//        countTags("c", cInput)
//        countTags("d", dInput)
//        countTags("e", eInput)

//        tagStat("b", bInput)
//        tagStat("c", cInput)
//        tagStat("d", dInput)
//        tagStat("e", eInput)


    }

    @Test
    internal fun testAll() {
        val solution = Solution1()
//        makeSlide("b", bInput, solution)
//        makeSlide("c", cInput, solution)
//        makeSlide("d", dInput, solution)
        makeSlide("e", eInput, solution)
    }
}