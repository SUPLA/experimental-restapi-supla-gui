// Copyright (C) AC SOFTWARE SP. Z O.O.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
package org.supla.gui.uidevice

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import org.slf4j.LoggerFactory
import org.supla.gui.api.ChannelApi
import pl.grzeslowski.jsupla.api.Color
import pl.grzeslowski.jsupla.api.channel.action.Action
import pl.grzeslowski.jsupla.api.channel.action.SetBrightnessAction
import pl.grzeslowski.jsupla.api.channel.action.SetBrightnessAndColor
import pl.grzeslowski.jsupla.api.channel.action.SetColorAction

class ColorStateChangeListener(channelApi: ChannelApi,
                               channel: UiChannel,
                               private val hue: SimpleDoubleProperty,
                               private val saturation: SimpleDoubleProperty,
                               private val value: SimpleDoubleProperty,
                               private val rgb: SimpleObjectProperty<javafx.scene.paint.Color>,
                               private val brightness: SimpleDoubleProperty?) : GenericListener<javafx.scene.paint.Color>(channelApi, channel) {
    private val logger = LoggerFactory.getLogger(ColorStateChangeListener::class.java)

    constructor(channelApi: ChannelApi,
                channel: UiChannel,
                hue: SimpleDoubleProperty,
                saturation: SimpleDoubleProperty,
                value: SimpleDoubleProperty,
                rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : this(channelApi, channel, hue, saturation, value, rgb, null)

    override fun buildAction(newValue: javafx.scene.paint.Color): Action {
        val color = Color.Rgb(
                newValue.red,
                newValue.green,
                newValue.blue
        )
        return if (brightness != null) {
            SetBrightnessAndColor(brightness.value.toInt(), color)
        } else {
            SetColorAction.setRgb(color)
        }
    }

    override fun rollback(oldValue: javafx.scene.paint.Color) {
        rgb.value = oldValue
    }

    override fun afterUpdate() {
        logger.trace("Setting HSV to {}|{}|{}", rgb.value.hue, rgb.value.saturation, rgb.value.brightness)
        hue.value = if (rgb.value.hue < 360.0) rgb.value.hue else 0.0
        saturation.value = rgb.value.saturation * 100.0
        value.value = rgb.value.brightness * 100.0
    }
}

abstract class DoubleStateChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        private val hue: SimpleDoubleProperty?,
        private val saturation: SimpleDoubleProperty?,
        private val value: SimpleDoubleProperty?,
        private val brightness: SimpleDoubleProperty?) : GenericListener<Number>(channelApi, channel) {
    override fun buildAction(newValue: Number): Action =
            if (hue != null && saturation != null && value != null) {
                val hsv = Color.Hsv(hue.value, saturation.value / 100.0, value.value / 100.0)
                if (brightness != null) {
                    SetBrightnessAndColor(brightness.value.toInt(), hsv)
                } else {
                    SetColorAction.setHsv(hsv)
                }
            } else if (brightness != null) {
                SetBrightnessAction(brightness.value.toInt())
            } else {
                throw IllegalStateException("Everything cannot be null!")
            }
}

abstract class HsvChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        private val hue: SimpleDoubleProperty,
        private val saturation: SimpleDoubleProperty,
        private val value: SimpleDoubleProperty,
        brightness: SimpleDoubleProperty?,
        private val rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : DoubleStateChangeListener(channelApi, channel, hue, saturation, value, brightness) {
    override fun afterUpdate() {
        rgb.value = javafx.scene.paint.Color.hsb(
                hue.value,
                saturation.value / 100.0,
                value.value / 100.0
        )
    }
}

class HueStateChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        private val hue: SimpleDoubleProperty,
        saturation: SimpleDoubleProperty,
        value: SimpleDoubleProperty,
        brightness: SimpleDoubleProperty?,
        rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : HsvChangeListener(channelApi, channel, hue, saturation, value, brightness, rgb) {
    constructor(channelApi: ChannelApi,
                channel: UiChannel,
                hue: SimpleDoubleProperty,
                saturation: SimpleDoubleProperty,
                value: SimpleDoubleProperty,
                rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : this(channelApi, channel, hue, saturation, value, null, rgb)

    override fun rollback(oldValue: Number) {
        hue.value = oldValue.toDouble()
    }
}

class SaturationStateChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        hue: SimpleDoubleProperty,
        private val saturation: SimpleDoubleProperty,
        value: SimpleDoubleProperty,
        brightness: SimpleDoubleProperty?,
        rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : HsvChangeListener(channelApi, channel, hue, saturation, value, brightness, rgb) {
    constructor(channelApi: ChannelApi,
                channel: UiChannel,
                hue: SimpleDoubleProperty,
                saturation: SimpleDoubleProperty,
                value: SimpleDoubleProperty,
                rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : this(channelApi, channel, hue, saturation, value, null, rgb)

    override fun rollback(oldValue: Number) {
        saturation.value = oldValue.toDouble()
    }
}

class ValueStateChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        hue: SimpleDoubleProperty,
        saturation: SimpleDoubleProperty,
        private val value: SimpleDoubleProperty,
        brightness: SimpleDoubleProperty?,
        rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : HsvChangeListener(channelApi, channel, hue, saturation, value, brightness, rgb) {
    constructor(channelApi: ChannelApi,
                channel: UiChannel,
                hue: SimpleDoubleProperty,
                saturation: SimpleDoubleProperty,
                value: SimpleDoubleProperty,
                rgb: SimpleObjectProperty<javafx.scene.paint.Color>) : this(channelApi, channel, hue, saturation, value, null, rgb)

    override fun rollback(oldValue: Number) {
        value.value = oldValue.toDouble()
    }
}

class ColorAndBrightnessStateChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        hue: SimpleDoubleProperty,
        saturation: SimpleDoubleProperty,
        value: SimpleDoubleProperty,
        private val brightness: SimpleDoubleProperty) : DoubleStateChangeListener(channelApi, channel, hue, saturation, value, brightness) {
    override fun rollback(oldValue: Number) {
        brightness.value = oldValue.toDouble()
    }
}

class BrightnessStateChangeListener(
        channelApi: ChannelApi,
        channel: UiChannel,
        private val brightness: SimpleDoubleProperty) : DoubleStateChangeListener(channelApi, channel, null, null, null, brightness) {
    override fun rollback(oldValue: Number) {
        brightness.value = oldValue.toDouble()
    }
}