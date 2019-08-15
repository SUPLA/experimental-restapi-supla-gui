package org.supla.gui

import griffon.javafx.test.FunctionalJavaFXRunner
import griffon.javafx.test.GriffonTestFXClassRule
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers

@RunWith(FunctionalJavaFXRunner::class)
class JSuplaGuiFunctionalTest {
    companion object {
        @ClassRule
        @JvmField
        val testfx: GriffonTestFXClassRule = GriffonTestFXClassRule("mainWindow")
    }

    @Ignore
    @Test
    fun _01_clickButton() {
        // given:
        FxAssert.verifyThat("#clickLabel", LabeledMatchers.hasText("0"))

        // when:
        testfx.clickOn("#clickActionTarget")

        // then:
        FxAssert.verifyThat("#clickLabel", LabeledMatchers.hasText("1"))
    }
}
