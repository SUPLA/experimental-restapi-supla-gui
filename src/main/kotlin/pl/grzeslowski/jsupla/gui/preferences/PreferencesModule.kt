package pl.grzeslowski.jsupla.gui.preferences

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor

import javax.inject.Named

@Named
@Suppress("unused")
@ServiceProviderFor(value = [Module::class])
class PreferencesModule : AbstractModule() {
    override fun doConfigure() {
        bind(PreferencesService::class.java).to(SimplePreferences::class.java).asSingleton()
        bind(TokenService::class.java).to(TokenServiceImpl::class.java).asSingleton()
    }
}