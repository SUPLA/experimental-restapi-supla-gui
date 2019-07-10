import griffon.util.AbstractMapResourceBundle

class Config : AbstractMapResourceBundle() {
    override fun initialize(entries: MutableMap<String, Any>) {
        entries.put("application", hashMapOf(
                "title" to "Supla",
                "startupGroups" to listOf("splashScreen"),
                "autoshutdown" to true
        ))
        entries.put("mvcGroups", hashMapOf(
                "jSuplaGui" to hashMapOf(
                        "model" to "pl.grzeslowski.jsupla.gui.JSuplaGuiModel",
                        "view" to "pl.grzeslowski.jsupla.gui.JSuplaGuiView",
                        "controller" to "pl.grzeslowski.jsupla.gui.JSuplaGuiController"
                ),
                "login" to hashMapOf(
                        "model" to "pl.grzeslowski.jsupla.gui.LoginModel",
                        "view" to "pl.grzeslowski.jsupla.gui.LoginView",
                        "controller" to "pl.grzeslowski.jsupla.gui.LoginController"),
                "splashScreen" to hashMapOf(
                        "model" to "pl.grzeslowski.jsupla.gui.SplashScreenModel",
                        "view" to "pl.grzeslowski.jsupla.gui.SplashScreenView",
                        "controller" to "pl.grzeslowski.jsupla.gui.SplashScreenController")
        ))
    }
}