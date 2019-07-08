package pl.grzeslowski.jsupla.gui.view.executor

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named

@Named
@Suppress("unused")
@ServiceProviderFor(value = [Module::class])
class ExecutorsModule : AbstractModule() {
    override fun doConfigure() {
        bind(OnOffExecutor::class.java).to(OnOffExecutor::class.java)
        bind(ColorExecutor::class.java).to(ColorExecutor::class.java)
        bind(RollerShutterExecutor::class.java).to(RollerShutterExecutor::class.java)
    }
}
