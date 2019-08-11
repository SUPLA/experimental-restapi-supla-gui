package pl.grzeslowski.jsupla.gui.i18n

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named


@Named
@ServiceProviderFor(value = [Module::class])
class InternationalizationModule : AbstractModule() {
    override fun doConfigure() {
        bind(InternationalizationService::class.java).to(InternationalizationServiceImpl::class.java).asSingleton()
    }
}