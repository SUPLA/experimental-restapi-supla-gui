package pl.grzeslowski.jsupla.gui.uidevice

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named

@Named
@Suppress("unused")
@ServiceProviderFor(value = [Module::class])
class ActionExecutorModule : AbstractModule() {
    override fun doConfigure() {
        bind(ActionExecutor::class.java).to(ActionExecutor::class.java).asSingleton()
    }
}