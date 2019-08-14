import griffon.util.AbstractMapResourceBundle

class Config : AbstractMapResourceBundle() {
    override fun initialize(entries: MutableMap<String, Any>) {
        entries.put("application", hashMapOf(
                "title" to "Supla",
                "startupGroups" to listOf("splashScreen", "jSuplaGui"),
                "autoshutdown" to true
        ))
        entries.put("mvcGroups", hashMapOf(
                "jSuplaGui" to hashMapOf(
                        "model" to "pl.grzeslowski.jsupla.gui.JSuplaGuiModel",
                        "view" to "pl.grzeslowski.jsupla.gui.JSuplaGuiView",
                        "controller" to "pl.grzeslowski.jsupla.gui.JSuplaGuiController"
                ),
                "splashScreen" to hashMapOf(
                        "model" to "pl.grzeslowski.jsupla.gui.SplashScreenModel",
                        "view" to "pl.grzeslowski.jsupla.gui.SplashScreenView",
                        "controller" to "pl.grzeslowski.jsupla.gui.SplashScreenController")
        ))
    }
}