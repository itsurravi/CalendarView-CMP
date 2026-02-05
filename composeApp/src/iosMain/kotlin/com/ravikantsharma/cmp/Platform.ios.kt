package com.ravikantsharma.cmp

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
//    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = UIDevice.currentDevice.name + " " +
        UIDevice.currentDevice.systemName + " " +
        UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()