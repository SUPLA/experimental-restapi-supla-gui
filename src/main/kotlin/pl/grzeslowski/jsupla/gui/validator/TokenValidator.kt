package pl.grzeslowski.jsupla.gui.validator

import com.jfoenix.validation.RequiredFieldValidator
import javafx.scene.control.TextInputControl
import java.net.MalformedURLException
import java.net.URI

class TokenValidator : RequiredFieldValidator() {

    override fun eval() {
        super.eval()
        if (hasErrors.get().not()) {
            val textField = srcControl.get() as TextInputControl
            val token = textField.text
            hasErrors.set(hasDot(token).not() || isUrlInToken(token).not())
        }
    }

    private fun hasDot(token: String) = token.contains(".")

    private fun isUrlInToken(token: String): Boolean {
        val url = token.split(".")[1]
        try {
            URI(url)
            return true
        } catch (ex: MalformedURLException) {
            return false
        }
    }
}