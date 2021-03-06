import java.awt.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

open class Command : AndroidToolUI() {
fun connectionCheck(){
    if (!CommandRunning) {
        GetStateOutput = exec("adb", "get-state", output = true)
        GetStateErrorOutput = exec("adb", "get-state", streamType = "Error", output = true)
        AdbDevicesOutput = exec("adb", "devices", output = true)
        FastbootDevicesOutput = exec("fastboot", "devices", output = true)
    }

    ConnectedViaFastboot = "fastboot" in FastbootDevicesOutput
    ConnectedViaRecovery = "recovery" in GetStateOutput
    ConnectedViaAdb = "device" in GetStateOutput
    ConnectedAdbUsb = "192.168" !in AdbDevicesOutput
    ConnectedAdbWifi = "offline" !in GetStateOutput
    UnauthorizedDevice = "unauthorized" in AdbDevicesOutput
    MultipleDevicesConnected = "error: more than one device/emulator" in GetStateErrorOutput


    if (MultipleDevicesConnected) {
        dialogUnauthorizedDevice.dispose()
        if (!dialogMultipleDevice.isVisible) {
            labelManufacturerValue.text = "-"
            labelBrandValue.text = "-"
            labelModelValue.text = "-"
            labelCodenameValue.text = "-"
            labelCPUValue.text = "-"
            labelCPUAValue.text = "-"
            labelSNValue.text = "-"
            labelGsmOperatorValue.text = "-"
            labelFingerprintValue.text = "-"
            labelVersionReleaseValue.text = "-"
            labelSDKValue.text = "-"
            labelSecurityPatchValue.text = "-"
            labelLanguageValue.text = "-"
            labelSelinuxValue.text = "-"
            labelTrebleValue.text = "-"
            labelUnlockValue.text = "-"
            labelFastbootCodenameValue.text = "-"
            labelFastbootSNValue.text = "-"
            labelSystemFSValue.text = "-"
            labelSystemCapacityValue.text = "-"
            labelDataFSValue.text = "-"
            labelDataCapacityValue.text = "-"
            labelBootFSValue.text = "-"
            labelBootCapacityValue.text = "-"
            labelRecoveryFSValue.text = "-"
            labelRecoveryCapacityValue.text = "-"
            labelCacheFSValue.text = "-"
            labelCacheCapacityValue.text = "-"
            labelVendorFSValue.text = "-"
            labelVendorCapacityValue.text = "-"
            labelAllCapacityValue.text = "-"
            labelUSBConnection.text = "Not connected"
            labelUSBConnection.icon = iconNo
            labelTCPConnection.text = "Not connected"
            labelTCPConnection.icon = iconNo
            listModelLogs.removeAllElements()
            frame.isEnabled = false
            dialogMultipleDevice.isVisible = true
        }
    } else if (UnauthorizedDevice) {
        if (!dialogUnauthorizedDevice.isVisible) {
            frame.isEnabled = false
            dialogUnauthorizedDevice.isVisible = true
        }
    }

    when {
        ConnectedViaAdb -> {
            if (FirstAdbConnection) {
                tabbedpane.selectedIndex = 0
                FirstAdbConnection = false
            }
            frame.isEnabled = true
            dialogUnauthorizedDevice.dispose()
            if (enabledAll) {
                val components: Array<Component> = fastbootPanel.components
                for (component in components) {
                    component.isEnabled = false
                }
                val components4: Array<Component> = recoveryPanel.components
                for (component in components4) {
                    component.isEnabled = false
                }
                val components2: Array<Component> = adbPanel.components
                for (component in components2) {
                    component.isEnabled = true
                }
                val components3: Array<Component> = logsPanel.components
                for (component in components3) {
                    if (component != buttonStop && component != buttonSave) {
                        component.isEnabled = true
                    }
                }
            }
            textAreaCommandFastbootOutput.isFocusable = false
            textAreaCommandOutput.isFocusable = true
            textAreaCommandInput.isFocusable = true
            textAreaCommandFastbootInput.isFocusable = false
            listLogs.isFocusable = true
            list.isFocusable = true

            if (newPhone) {
                getProp()
            }
            if (ConnectedAdbUsb) {
                labelUSBConnection.text = "Connected via Adb"
                labelUSBConnection.icon = iconYes
                buttonIpConnect.isEnabled = false
                labelIP.isEnabled = false
                textFieldIP.isEnabled = false
            } else {
                if (ConnectedAdbWifi) {
                    labelTCPConnection.text = "Connected to ${AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')}"
                    labelTCPConnection.icon = iconYes
                    labelConnect.text = ""
                }
            }
            newPhone = false
            enabledAll = false
        }
        ConnectedViaFastboot -> {
            if (FirstFastbootConnection) {
                tabbedpane.selectedIndex = 2
                FirstFastbootConnection = false
            }
            if (enabledAll) {
                val components: Array<Component> = fastbootPanel.components
                for (component in components) {
                    component.isEnabled = true
                }
                val components2: Array<Component> = adbPanel.components
                for (component in components2) {
                    component.isEnabled = false
                }
                val components4: Array<Component> = recoveryPanel.components
                for (component in components4) {
                    component.isEnabled = false
                }
                val components3: Array<Component> = logsPanel.components
                for (component in components3) {
                    component.isEnabled = false
                }
            }
            textAreaCommandFastbootOutput.isFocusable = true
            textAreaCommandOutput.isFocusable = false
            textAreaCommandInput.isFocusable = false
            textAreaCommandFastbootInput.isFocusable = true
            listLogs.isFocusable = false
            list.isFocusable = false

            if (newPhone) {
                getPropFastboot()
                labelUSBConnection.text = "Connected via Fastboot"
                labelUSBConnection.icon = iconYes
            }
            newPhone = false
            enabledAll = false
        }
        ConnectedViaRecovery -> {
            if (FirstRecoveryConnection) {
                tabbedpane.selectedIndex = 3
                FirstRecoveryConnection = false
            }
            frame.isEnabled = true
            dialogUnauthorizedDevice.dispose()
            if (enabledAll) {
                val components: Array<Component> = fastbootPanel.components
                for (component in components) {
                    component.isEnabled = false
                }
                val components2: Array<Component> = adbPanel.components
                for (component in components2) {
                    component.isEnabled = false
                }
                val components4: Array<Component> = recoveryPanel.components
                for (component in components4) {
                    component.isEnabled = true
                }
                val components3: Array<Component> = logsPanel.components
                for (component in components3) {
                    if (component != buttonStop && component != buttonSave) {
                        component.isEnabled = false
                    }
                }
            }
            textAreaCommandFastbootOutput.isFocusable = false
            textAreaCommandOutput.isFocusable = false
            textAreaCommandInput.isFocusable = false
            textAreaCommandFastbootInput.isFocusable = false
            listLogs.isFocusable = false
            list.isFocusable = false

            if (newPhone) {
                getPropRecovery()
                labelUSBConnection.text = "Connected via Adb"
                labelUSBConnection.icon = iconYes
            }
            newPhone = false
            enabledAll = false
        }
        else -> {
            FirstFastbootConnection = true
            FirstAdbConnection = true
            FirstRecoveryConnection = true
            enabledAll = true
            newPhone = true
            if (!UnauthorizedDevice) {
                frame.isEnabled = true
                dialogUnauthorizedDevice.dispose()
            }
            noConnection()
        }
    }
}
    private fun getProp() {
        val deviceProps = exec("adb", "shell getprop", output = true)
        val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [").substringBefore(']')
        Manufacturer = if (!lineValue1.isBlank()) {
            lineValue1
        } else {
            "Unknown"
        }
        val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [").substringBefore(']')
        Brand = if (!lineValue2.isBlank()) {
            lineValue2
        } else {
            "Unknown"
        }
        val lineValue3 = deviceProps.substringAfter("ro.product.model]: [").substringBefore(']')
        Model = if (!lineValue3.isBlank()) {
            lineValue3
        } else {
            "Unknown"
        }
        val lineValue4 = deviceProps.substringAfter("ro.product.name]: [").substringBefore(']')
        Codename = if (!lineValue4.isBlank()) {
            lineValue4
        } else {
            "Unknown"
        }
        val lineValue5 = deviceProps.substringAfter("ro.product.board]: [").substringBefore(']')
        CPU = if (!lineValue5.isBlank()) {
            lineValue5
        } else {
            "Unknown"
        }
        val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [").substringBefore(']')
        CPUA = if (!lineValue6.isBlank()) {
            lineValue6
        } else {
            "Unknown"
        }
        var lineValue7 = deviceProps.substringAfter("ro.serialno]: [").substringBefore(']')
        SN = if (!lineValue7.isBlank()) {
            lineValue7
        } else {
            "Unknown"
        }
        var lineValue8 = deviceProps.substringAfter("gsm.operator.alpha]: [").substringBefore(']')
        GsmOperator = if (!lineValue8.isBlank() && lineValue8 != ",") {
            lineValue8
        } else {
            "Unknown"
        }
        var lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [").substringBefore(']')
        Fingerprint = if (!lineValue9.isBlank()) {
            lineValue9
        } else {
            "Unknown"
        }
        var lineValue10 = deviceProps.substringAfter("ro.build.version.release]: [").substringBefore(']')
        VersionRelease = if (!lineValue10.isBlank()) {
            lineValue10
        } else {
            "Unknown"
        }
        var lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [").substringBefore(']')
        SDK = if (!lineValue11.isBlank()) {
            lineValue11
        } else {
            "Unknown"
        }
        var lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [").substringBefore(']')
        SecurityPatch = if (!lineValue12.isBlank()) {
            lineValue12
        } else {
            "Unknown"
        }
        var lineValue13 = deviceProps.substringAfter("ro.product.locale]: [").substringBefore(']')
        Language = if (!lineValue13.isBlank()) {
            lineValue13
        } else {
            "Unknown"
        }
        var lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [").substringBefore(']')
        Selinux = if (!lineValue14.isBlank() && "DEVICE" !in lineValue14) {
            lineValue14
        } else {
            "Unknown"
        }
        var lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [").substringBefore(']')
        Treble = if (!lineValue15.isBlank()) {
            lineValue15
        } else {
            "Unknown"
        }
        labelManufacturerValue.text = Manufacturer
        labelBrandValue.text = Brand
        labelModelValue.text = Model
        labelCodenameValue.text = Codename
        labelCPUValue.text = CPU
        labelCPUAValue.text = CPUA
        labelSNValue.text = SN
        labelGsmOperatorValue.text = GsmOperator
        labelFingerprintValue.text = Fingerprint
        labelVersionReleaseValue.text = VersionRelease
        labelSDKValue.text = SDK
        labelSecurityPatchValue.text = SecurityPatch
        labelLanguageValue.text = Language
        labelSelinuxValue.text = Selinux
        labelTrebleValue.text = Treble
    }

    private fun getPropRecovery() {
        val deviceProps = exec("adb", "shell getprop", output = true)
        val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [").substringBefore(']')
        Manufacturer = if (!lineValue1.isBlank()) {
            lineValue1
        } else {
            "Unknown"
        }
        val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [").substringBefore(']')
        Brand = if (!lineValue2.isBlank()) {
            lineValue2
        } else {
            "Unknown"
        }
        val lineValue3 = deviceProps.substringAfter("ro.product.model]: [").substringBefore(']')
        Model = if (!lineValue3.isBlank()) {
            lineValue3
        } else {
            "Unknown"
        }
        val lineValue4 = deviceProps.substringAfter("ro.product.name]: [").substringBefore(']')
        Codename = if (!lineValue4.isBlank()) {
            lineValue4
        } else {
            "Unknown"
        }
        val lineValue5 = deviceProps.substringAfter("ro.boot.hardware]: [").substringBefore(']')
        CPU = if (!lineValue5.isBlank()) {
            lineValue5
        } else {
            "Unknown"
        }
        val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [").substringBefore(']')
        CPUA = if (!lineValue6.isBlank()) {
            lineValue6
        } else {
            "Unknown"
        }
        val lineValue7 = deviceProps.substringAfter("ro.serialno]: [").substringBefore(']')
        SN = if (!lineValue7.isBlank()) {
            lineValue7
        } else {
            "Unknown"
        }
        val lineValue8 = deviceProps.substringAfter("sys.usb.state]: [").substringBefore(']')
        GsmOperator = if (!lineValue8.isBlank()) {
            lineValue8
        } else {
            "Unknown"
        }
        val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [").substringBefore(']')
        Fingerprint = if (!lineValue9.isBlank()) {
            lineValue9
        } else {
            "Unknown"
        }
        var lineValue10 = deviceProps.substringAfter("ro.orangefox.version]: [").substringBefore(']')
        if (!lineValue10.isBlank()) {
            VersionRelease = lineValue10
        } else {
            lineValue10 = deviceProps.substringAfter("ro.twrp.version]: [").substringBefore(']')
            VersionRelease = if (!lineValue10.isBlank()) {
                lineValue10
            } else {
                "Unknown"
            }
        }
        val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [").substringBefore(']')
        SDK = if (!lineValue11.isBlank()) {
            lineValue11
        } else {
            "Unknown"
        }
        val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [").substringBefore(']')
        SecurityPatch = if (!lineValue12.isBlank()) {
            lineValue12
        } else {
            "Unknown"
        }
        val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [").substringBefore(']')
        Language = if (!lineValue13.isBlank()) {
            lineValue13
        } else {
            "Unknown"
        }
        val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [").substringBefore(']')
        Selinux = if (!lineValue14.isBlank() && "DEVICE" !in lineValue14) {
            lineValue14
        } else {
            "Unknown"
        }
        val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [").substringBefore(']')
        Treble = if (!lineValue15.isBlank()) {
            lineValue15
        } else {
            "Unknown"
        }
        val lineValue17 = deviceProps.substringAfter("ro.boot.secureboot]: [").substringBefore(']')
        SecureBoot = if (!lineValue17.isBlank()) {
            if (lineValue17 == "1") {
                "true"
            } else {
                "false"
            }
        } else {
            "Unknown"
        }
        val lineValue18 = deviceProps.substringAfter("ro.build.host]: [").substringBefore(']')
        DeviceHost = if (!lineValue18.isBlank() && "DEVICE" !in lineValue14) {
            lineValue18
        } else {
            "Unknown"
        }
        val lineValue16 = deviceProps.substringAfter("ro.allow.mock.location]: [").substringBefore(']')
        MockLocation = if (!lineValue16.isBlank()) {
            if (lineValue16 == "1") {
                "true"
            } else {
                "false"
            }
        } else {
            "Unknown"
        }
        val lineValue19 = deviceProps.substringAfter("ro.build.id]: [").substringBefore(']')
        Language = if (!lineValue19.isBlank()) {
            lineValue19
        } else {
            "Unknown"
        }
        labelManufacturerValue.text = Manufacturer
        labelBrandValue.text = Brand
        labelModelValue.text = Model
        labelCodenameValue.text = Codename
        labelCPUValue.text = CPU
        labelCPUAValue.text = CPUA
        labelSNValue.text = SN
        labelGsmOperatorValue.text = GsmOperator
        labelFingerprintValue.text = Fingerprint
        labelVersionReleaseValue.text = VersionRelease
        labelSDKValue.text = SDK
        labelSecurityPatchValue.text = SecurityPatch
        labelLanguageValue.text = Language
        labelSelinuxValue.text = Selinux
        labelTrebleValue.text = Treble
        labelDeviceHostnameValue.text = DeviceHost
        labelSecureBootValue.text = SecureBoot
        labelLocationsValue.text = MockLocation
    }

    private fun getPropFastboot() {
        var fastbootProps = exec("fastboot", "getvar all", output = true, streamType = "Error")
        Unlock = fastbootProps.substringAfter("(bootloader) unlocked:").substringBefore( "(bootloader) ").trimMargin()
        FastbootCodename = fastbootProps.substringAfter("(bootloader) product:").substringBefore( "(bootloader) ").trimMargin()
        FastbootSN = fastbootProps.substringAfter("(bootloader) serialno:").substringBefore( "(bootloader) ").trimMargin()
        SystemFS = fastbootProps.substringAfter("(bootloader) partition-type:system:"). substringBefore( "(bootloader) ").trimMargin()
        val SystemDec = fastbootProps.substringAfter("(bootloader) partition-size:system: 0x").substringBefore("(bootloader) ").trimMargin()
        SystemCapacity = (java.lang.Long.parseLong(SystemDec, 16) / 1048576).toString().trimMargin()
        DataFS = fastbootProps.substringAfter("(bootloader) partition-type:userdata:"). substringBefore( "(bootloader) ").trimMargin()
        val DataDec = fastbootProps.substringAfter("(bootloader) partition-size:userdata: 0x").substringBefore( "(bootloader) ").trimMargin()
        DataCapacity = (java.lang.Long.parseLong(DataDec, 16) / 1048576).toString().trimMargin()
        BootFS = fastbootProps.substringAfter("(bootloader) partition-type:boot:"). substringBefore( "(bootloader) ").trimMargin()
        val BootDec = fastbootProps.substringAfter("(bootloader) partition-size:boot: 0x").substringBefore( "(bootloader) ").trimMargin()
        BootCapacity = (java.lang.Long.parseLong(BootDec, 16) / 1048576).toString().trimMargin()
        RecoveryFS = fastbootProps.substringAfter("(bootloader) partition-type:recovery:"). substringBefore( "(bootloader) ").trimMargin()
        val RecoveryDec = fastbootProps.substringAfter("(bootloader) partition-size:recovery: 0x").substringBefore( "(bootloader) ").trimMargin()
        RecoveryCapacity = (java.lang.Long.parseLong(RecoveryDec, 16) / 1048576).toString().trimMargin()
        CacheFS = fastbootProps.substringAfter("(bootloader) partition-type:cache:"). substringBefore( "(bootloader) ").trimMargin()
        val CacheDec = fastbootProps.substringAfter("(bootloader) partition-size:cache: 0x").substringBefore( "(bootloader) ").trimMargin()
        CacheCapacity = (java.lang.Long.parseLong(CacheDec, 16) / 1048576).toString().trimMargin()
        VendorFS = fastbootProps.substringAfter("(bootloader) partition-type:vendor:"). substringBefore( "(bootloader) ").trimMargin()
        val VendorDec = fastbootProps.substringAfter("(bootloader) partition-size:vendor: 0x").substringBefore( "(bootloader) ").trimMargin()
        VendorCapacity = (java.lang.Long.parseLong(VendorDec, 16) / 1048576).toString()
        AllCapacity = (SystemCapacity.toInt() + DataCapacity.toInt() + BootCapacity.toInt() + RecoveryCapacity.toInt() + CacheCapacity.toInt() + VendorCapacity.toInt()).toString()
        labelUnlockValue.text = if (Unlock != "< waiting for any device >") {
            Unlock
        } else {
            "-"
        }
        labelFastbootCodenameValue.text = if (FastbootCodename != "< waiting for any device >") {
            FastbootCodename
        } else {
            "-"
        }
        labelFastbootSNValue.text = if (FastbootSN != "< waiting for any device >") {
            FastbootSN
        } else {
            "-"
        }
        labelSystemFSValue.text = if (SystemFS != "< waiting for any device >") {
            SystemFS
        } else {
            "-"
        }
        labelSystemCapacityValue.text = if (SystemCapacity != "< waiting for any device >") {
            SystemCapacity
        } else {
            "-"
        }
        labelDataFSValue.text = if (DataFS != "< waiting for any device >") {
            DataFS
        } else {
            "-"
        }
        labelDataCapacityValue.text = if (DataCapacity != "< waiting for any device >") {
            DataCapacity
        } else {
            "-"
        }
        labelBootFSValue.text = if (BootFS != "< waiting for any device >") {
            BootFS
        } else {
            "-"
        }
        labelBootCapacityValue.text = if (BootCapacity != "< waiting for any device >") {
            BootCapacity
        } else {
            "-"
        }
        labelRecoveryFSValue.text = if (RecoveryFS != "< waiting for any device >") {
            RecoveryFS
        } else {
            "-"
        }
        labelRecoveryCapacityValue.text = if (RecoveryCapacity != "< waiting for any device >") {
            RecoveryCapacity
        } else {
            "-"
        }
        labelCacheFSValue.text = if (CacheFS != "< waiting for any device >") {
            CacheFS
        } else {
            "-"
        }
        labelCacheCapacityValue.text = if (CacheCapacity != "< waiting for any device >") {
            CacheCapacity
        } else {
            "-"
        }
        labelVendorFSValue.text = if (VendorFS != "< waiting for any device >") {
            VendorFS
        } else {
            "-"
        }
        labelVendorCapacityValue.text = if (VendorCapacity != "< waiting for any device >") {
            VendorCapacity
        } else {
            "-"
        }
        labelAllCapacityValue.text = if (AllCapacity != "< waiting for any device >") {
            AllCapacity
        } else {
            "-"
        }
    }
    fun exec(app: String, command: String, output: Boolean = false, streamType: String = "Input"): String {
        try {
            val process = Runtime.getRuntime().exec("$WorkingDir$app $command")
            if (output) {
                return if (streamType == "Input")
                    process.inputStream.bufferedReader().readText()
                else
                    process.errorStream.bufferedReader().readText()
            }
            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
    fun execLines(command: String): List<String> {
        val process = Runtime.getRuntime().exec("$WorkingDir$command")
        return process.inputStream.bufferedReader().readLines()
    }

    private fun noConnection() {
        val components: Array<Component> = fastbootPanel.components
        for (component in components) {
            component.isEnabled = false
        }
        val components2: Array<Component> = adbPanel.components
        for (component in components2) {
            component.isEnabled = false
        }
        val components4: Array<Component> = recoveryPanel.components
        for (component in components4) {
            component.isEnabled = false
        }
        val components3: Array<Component> = logsPanel.components
        for (component in components3) {
            if(component != buttonStop && component != buttonSave) {
                component.isEnabled = false
            }
        }
        buttonReboot.isEnabled = false
        buttonRecoveryReboot.isEnabled = false
        buttonFastbootReboot.isEnabled = false
        buttonPowerOff.isEnabled = false
        textAreaCommandFastbootOutput.isFocusable = false
        textAreaCommandOutput.isFocusable = false
        textAreaCommandInput.isFocusable = false
        textAreaCommandFastbootInput.isFocusable = false
        listLogs.isFocusable = false
        list.isFocusable = false
        listModel.removeAllElements()
        listModelLogs.removeAllElements()
        labelManufacturerValue.text = "-"
        labelBrandValue.text = "-"
        labelModelValue.text = "-"
        labelCodenameValue.text = "-"
        labelCPUValue.text = "-"
        labelCPUAValue.text = "-"
        labelSNValue.text = "-"
        labelGsmOperatorValue.text = "-"
        labelFingerprintValue.text = "-"
        labelVersionReleaseValue.text = "-"
        labelSDKValue.text = "-"
        labelSecurityPatchValue.text = "-"
        labelLanguageValue.text = "-"
        labelSelinuxValue.text = "-"
        labelTrebleValue.text = "-"
        labelUnlockValue.text = "-"
        labelFastbootCodenameValue.text = "-"
        labelFastbootSNValue.text = "-"
        labelSystemFSValue.text = "-"
        labelSystemCapacityValue.text = "-"
        labelDataFSValue.text = "-"
        labelDataCapacityValue.text = "-"
        labelBootFSValue.text = "-"
        labelBootCapacityValue.text = "-"
        labelRecoveryFSValue.text = "-"
        labelRecoveryCapacityValue.text = "-"
        labelCacheFSValue.text = "-"
        labelCacheCapacityValue.text = "-"
        labelVendorFSValue.text = "-"
        labelVendorCapacityValue.text = "-"
        labelAllCapacityValue.text = "-"
        textFieldIPa.text = ""
        textFieldIPa.isEnabled = false
        buttonIpConnect.isEnabled = true
        labelIP.isEnabled = true
        textFieldIP.isEnabled = true
        labelUSBConnection.text = "Not connected"
        labelUSBConnection.icon = iconNo
    }
}