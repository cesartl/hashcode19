package com.ctl.hash19

import com.ctl.hash19.InputUtils.getLines
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream

class PhotoTest {



    @Test
    internal fun parse() {
        val example = getLines("c_memorable_moments.txt")
        val photos = Photos.parse(example)
        println()
    }

    @Test
    internal fun writeSlideSHow() {
        val p = HPhoto(1, setOf())
        val s = HSlide(p)
        val show = SlideShow(listOf(s))
        show.write("foo.txt")
    }
}