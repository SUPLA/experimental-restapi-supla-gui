package pl.grzeslowski.jsupla.gui.view

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named

@Named
@ServiceProviderFor(value = [Module::class])
internal class ViewBuilderModule : AbstractModule() {
    override fun doConfigure() {
        bind(ViewBuilder::class.java).to(ViewBuilderImpl::class.java).asSingleton()
    }
}
