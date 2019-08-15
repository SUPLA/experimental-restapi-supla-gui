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

        bind(GateDeviceViewBuilder::class.java).to(GateDeviceViewBuilder::class.java)
        bind(LightDeviceViewBuilder::class.java).to(LightDeviceViewBuilder::class.java)
        bind(RgbDeviceViewBuilder::class.java).to(RgbDeviceViewBuilder::class.java)
        bind(RollerShutterDeviceViewBuilder::class.java).to(RollerShutterDeviceViewBuilder::class.java)
        bind(TemperatureAndHumidityDeviceViewBuilder::class.java).to(TemperatureAndHumidityDeviceViewBuilder::class.java)
    }
}
