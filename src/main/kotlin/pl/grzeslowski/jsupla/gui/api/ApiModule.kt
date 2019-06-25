package pl.grzeslowski.jsupla.gui.api

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor
import pl.grzeslowski.jsupla.api.Api
import javax.inject.Named

@Named
@ServiceProviderFor(value = [Module::class])
class ApiModule : AbstractModule() {
    override fun doConfigure() {
        bind(Api::class.java).toProvider(ApiProvider::class.java)
        bind(DeviceApi::class.java).to(SuplaDeviceApi::class.java)
        bind(ChannelApi::class.java).to(SuplaChannelApi::class.java)
        bind(LocationApi::class.java).to(SuplaLocationApi::class.java)
        bind(ServerApi::class.java).to(SuplaServerApi::class.java)
    }
}