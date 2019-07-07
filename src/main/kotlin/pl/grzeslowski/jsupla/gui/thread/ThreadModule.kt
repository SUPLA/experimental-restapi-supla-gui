package pl.grzeslowski.jsupla.gui.thread

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named

@Named
@Suppress("unused")
@ServiceProviderFor(value = [Module::class])
class ThreadModule : AbstractModule() {
    override fun doConfigure() {
        bind(ThreadService::class.java).to(ThreadServiceImpl::class.java).asSingleton()
    }
}