package com.ctl.hash19

import java.io.InputStream

object InputUtils{
    fun getStream(name: String): InputStream = this.javaClass.classLoader.getResourceAsStream(name)

    fun getLines(name: String): List<String> = getStream(name).bufferedReader().use { it.readLines() }
}