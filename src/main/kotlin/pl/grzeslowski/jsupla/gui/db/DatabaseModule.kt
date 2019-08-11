package pl.grzeslowski.jsupla.gui.db

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import javax.inject.Named

@Named
@ServiceProviderFor(value = [Module::class])
class DatabaseModule : AbstractModule() {
    override fun doConfigure() {
        bind(Database::class.java).to(InMemoryDatabase::class.java).asSingleton()
    }
}