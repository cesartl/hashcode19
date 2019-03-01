package com.ctl.hash19

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.IllegalArgumentException


sealed class Photo {
    abstract val tags: Set<String>
    abstract val id: Int

    companion object {
        fun parse(id: Int, line: String): Photo {
            val split = line.split(" ")
            val tags = split.drop(2).toSet()
            return if (split[0] == "H") {
                HPhoto(id, tags)
            } else {
                VPhoto(id, tags)
            }
        }
    }
}

data class HPhoto(override val id: Int, override val tags: Set<String>) : Photo()
data class VPhoto(override val id: Int, override val tags: Set<String>) : Photo()

sealed class Slide {
    abstract val tags: Set<String>
    abstract val id: String
}

data class HSlide(val hPhoto: HPhoto) : Slide() {
    override val id: String
        get() = hPhoto.id.toString()
    override val tags: Set<String>
        get() = hPhoto.tags
}


data class VSlide(val left: VPhoto, val right: VPhoto) : Slide() {
    override val id: String by lazy {
        "${left.id} ${right.id}"
    }

    override val tags: Set<String> by lazy {
        left.tags + right.tags
    }
}


data class SlideShow(val slides: List<Slide>) {
    fun score(): Int {
        return slides.fold(slides.first() to 0) { (prev, total), slide -> slide to Photos.scoreSlides(prev, slide) + total }.second
    }

    fun write(fileName: String) {
        File(fileName).bufferedWriter().use { out ->
            out.write(slides.size.toString())
            out.newLine()
            slides.forEach {
                when (it) {
                    is HSlide -> {
                        out.write(it.hPhoto.id.toString())
                        out.newLine()
                    }
                    is VSlide -> {
                        out.write("${it.left.id} ${it.right.id}")
                        out.newLine()
                    }
                }
            }
            out.flush()
        }
    }
}

object Photos {

    fun scoreSlides(prev: Slide, next: Slide): Int {
        val union = prev.tags.intersect(next.tags)
        val l = prev.tags.minus(next.tags)
        val r = next.tags.minus(prev.tags)
        return Math.min(union.size, Math.min(l.size, r.size))
    }

    fun parse(lines: List<String>): List<Photo> {
        return lines.drop(1).mapIndexed { idx, it -> Photo.parse(idx, it) }
    }
}




