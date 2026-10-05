package com.opendash.app.voice.stt

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class VoskResultParserTest {

    @Test
    fun `parses final transcript`() {
        assertThat(VoskResultParser.finalText("""{"text":"What is two plus two?"}"""))
            .isEqualTo("What is two plus two?")
    }

    @Test
    fun `parses partial transcript`() {
        assertThat(VoskResultParser.partialText("""{"partial":"what is"}"""))
            .isEqualTo("what is")
    }

    @Test
    fun `blank transcript is ignored`() {
        assertThat(VoskResultParser.finalText("""{"text":"  "}""")).isNull()
    }

    @Test
    fun `decodes escaped quotes`() {
        assertThat(VoskResultParser.finalText("""{"text":"say \"hello\""}"""))
            .isEqualTo("say \"hello\"")
    }
}
