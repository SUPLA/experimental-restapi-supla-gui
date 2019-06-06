package pl.grzeslowski.jsupla.gui.io

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named

@Named
@ServiceProviderFor(value = [Module::class])
class InputOutputModule : AbstractModule() {
    override fun doConfigure() {
        bind(InputOutputService::class.java).to(MacOsInputOutputService::class.java).asSingleton()
    }
}