import java.awt.Rectangle
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JFileChooser
import javax.swing.SwingWorker
import javax.swing.filechooser.FileNameExtensionFilter


var arrayList = emptyArray<String>()
var selectedDirectoryPath = ""
var selectedFileAbsolutePath = ""
var selectedFilePath = ""
var selectedZipPath = ""
var listModel = DefaultListModel<Any?>()
var listModelLogs = DefaultListModel<Any?>()
var stars: ArrayList<Any> = ArrayList()
var Manufacturer = ""
var Brand = ""
var Model = ""
var Codename = ""
var CPU = ""
var CPUA = ""
var SN = ""
var GsmOperator = ""
var Fingerprint = ""
var VersionRelease = ""
var SDK = ""
var SecurityPatch = ""
var Language = ""
var Selinux = ""
var Treble = ""
var DeviceHost = ""
var SecureBoot = ""
var MockLocation = ""
var Unlock = ""
var FastbootCodename = ""
var FastbootSN = ""
var SystemFS = ""
var SystemCapacity = ""
var DataFS = ""
var DataCapacity = ""
var BootFS = ""
var BootCapacity = ""
var RecoveryFS = ""
var RecoveryCapacity = ""
var CacheFS = ""
var CacheCapacity = ""
var VendorFS = ""
var VendorCapacity = ""
var AllCapacity = ""
var newPhone = true
var enabledAll = true
var GetStateOutput = ""
var GetStateErrorOutput = ""
var AdbDevicesOutput = ""
var FastbootDevicesOutput = ""
var ConnectedViaAdb = false
var ConnectedViaFastboot = false
var ConnectedViaRecovery = false
var UnauthorizedDevice = false
var MultipleDevicesConnected = false
var CommandRunning = false
var ConnectedAdbUsb = false
var ConnectedAdbWifi = false
var FirstFastbootConnection = true
var FirstRecoveryConnection = true
var FirstAdbConnection = true
var iconYes = ImageIcon(AndroidTool()::class.java.getResource("/icon/check.png"))
var iconNo = ImageIcon(AndroidTool()::class.java.getResource("/icon/not.png"))
val Windows = "Windows" in System.getProperty("os.name")
val Linux = "Linux" in System.getProperty("os.name")
val MacOS = "Mac" in System.getProperty("os.name")
val WorkingDir = System.getProperty("user.dir") + if (Windows) { "\\sdk-tools\\"} else { "/sdk-tools/" }
open class AndroidTool : Command() {
    init {
        AndroidToolUI()
        Command()
    }
    companion object : AndroidTool() {
        @JvmStatic
        fun main(args: Array<String>) {
            buttonIpConnect.addActionListener {
                labelConnect.text = ""
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonIpConnect.isEnabled = false
                        exec("adb", "kill-server")
                        val output = exec("adb", "connect ${textFieldIP.text}", output = true)
                        if ("connected to" in output) {
                            labelTCPConnection.text = "Connected to ${textFieldIP.text}"
                            labelTCPConnection.icon = iconYes
                        } else {
                            labelConnect.text = "Failed"
                        }
                    }
                    override fun done() {
                        buttonIpConnect.isEnabled = true
                    }
                }
                Worker().execute()
            }



            fun searchFilter(searchTerm: String) {
                val filteredItems: DefaultListModel<Any?> = DefaultListModel()
                val stars = stars
                stars.stream().forEach { star: Any ->
                    val starName = star.toString().toLowerCase()
                    if (starName.contains(searchTerm.toLowerCase())) {
                        if (!filteredItems.contains(star)) {
                        }
                        filteredItems.addElement(star)
                    }
                }
                listModel = filteredItems
                list.model = listModel
            }

            textFieldIPa.addKeyListener(object : KeyAdapter() {
                override fun keyReleased(evt: KeyEvent) {
                    searchFilter(textFieldIPa.text)
                }
            })





            buttonSave.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonSave.isEnabled = false
                        val choseFile = JFileChooser()
                        choseFile.dialogTitle = "Save logs file"
                        choseFile.addChoosableFileFilter(FileNameExtensionFilter("Logs File (.log)", "log"))
                        choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
                        choseFile.fileFilter = choseFile.choosableFileFilters[1]
                        val chooseDialog = choseFile.showSaveDialog(frame)
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            val file = File(choseFile.selectedFile.canonicalPath.toString() + "." + (choseFile.fileFilter as FileNameExtensionFilter).extensions[0])
                            if (!file.exists()) {
                                file.createNewFile()
                            }
                            val fw = FileWriter(file.absoluteFile)
                            val bw = BufferedWriter(fw)
                            for (element in 0 until listModelLogs.size()) {
                                bw.write(listModelLogs[element].toString())
                                bw.write("\n")
                            }
                            bw.close()
                        }
                    }

                    override fun done() {
                        buttonSave.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }
            var functionButtonStart = true
            var ifStopSelected = false
            var logsWorking: Boolean
            buttonStart.addActionListener {
                buttonStop.isEnabled =true
                buttonSave.isEnabled = false
                if (ifStopSelected) {
                    functionButtonStart = true
                }
                if (functionButtonStart) {
                    buttonSave.isEnabled = false
                    logsWorking = true
                    buttonStart.text = "Pause"
                    if (ifStopSelected) {
                        listModelLogs.removeAllElements()
                    }
                    class MyWorker : SwingWorker<Unit, Int>() {
                        override fun doInBackground() {
                            arrayList = emptyArray()
                            when {
                                radioButtonVerbose.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:V")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonDebug.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:D")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonInfo.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:I")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonWarning.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:W")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonError.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:E")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonFatal.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:F")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonSilent.isSelected -> {
                                    Runtime.getRuntime().exec("${WorkingDir}adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("${WorkingDir}adb logcat *:S")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (logsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val worker = MyWorker()
                    worker.execute()
                    functionButtonStart = false
                    ifStopSelected = false
                } else {
                    if (ifStopSelected) {
                        listModelLogs.removeAllElements()
                    } else {
                        logsWorking = false
                        functionButtonStart = true
                        buttonStart.text = "Continue"
                    }
                }
            }

            buttonStop.addActionListener {
                buttonStop.isEnabled = false
                buttonStart.text = "Start"
                logsWorking = false
                ifStopSelected = true
                buttonSave.isEnabled = true
                functionButtonStart = true
            }



            buttonReboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonReboot.isEnabled = false
                        when (tabbedpane.selectedIndex) {
                            0, 1 -> exec("adb", "reboot")
                            2 -> exec("fastboot", "reboot")
                            3 -> exec("adb", "shell twrp reboot")
                        }
                    }
                    override fun done() {
                        buttonReboot.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonRecoveryReboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRecoveryReboot.isEnabled = false
                        when (tabbedpane.selectedIndex) {
                            0, 1 -> exec("adb", "reboot recovery")
                            2 -> exec("fastboot", "oem reboot-recovery")
                            3 -> exec("adb", "shell twrp reboot recovery")
                        }
                    }

                    override fun done() {
                        buttonRecoveryReboot.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonFastbootReboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonFastbootReboot.isEnabled = false
                        when (tabbedpane.selectedIndex) {
                            0, 1 -> exec("adb", "reboot bootloader")
                            2 -> exec("fastboot", "reboot-bootloader")
                            3 -> exec("adb", "shell twrp reboot bootloader")
                        }
                    }

                    override fun done() {
                        buttonFastbootReboot.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonPowerOff.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonPowerOff.isEnabled = false
                        when (tabbedpane.selectedIndex) {
                            0, 1 -> exec("adb", "reboot -p")
                            3 -> exec("adb", "shell twrp reboot poweroff")
                        }
                    }

                    override fun done() {
                        buttonPowerOff.isEnabled = true
                    }
                }
                Worker().execute()
            }

            dialogUnauthorizedDevice.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    frame.isEnabled = true
                }
            })


            val run = Thread {
                while (true) {
                    try {
                        connectionCheck()
                        Thread.sleep(1000)
                    } catch (ex: InterruptedException) {
                    }
                }
            }
            run.start()


            tabbedpane.addChangeListener {
                try {
                    textFieldIP.text = AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
                } catch (e: Exception) { }
                if (tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1) {
                    labelGsmOperator.text = "Cellular provider:"
                    labelGsmOperator.bounds = Rectangle(15, 156, 110, 20)
                    labelGsmOperatorValue.bounds = Rectangle(125, 156, 155, 20)
                    labelCPU.text = "CPU:"
                    labelCPU.bounds = Rectangle(15, 96, 30, 20)
                    labelCPUValue.bounds = Rectangle(50, 96, 230, 20)
                    labelVersionRelease.text = "Android version:"
                    labelVersionReleaseValue.bounds = Rectangle(120, 34, 160, 20)
                    labelLanguage.text = "Language:"
                    labelLanguageValue.setBounds(85, 93, 195, 20)
                    labelSecureBoot.isVisible = false
                    labelDeviceHostname.isVisible = false
                    labelLocations.isVisible = false
                    labelSecureBootValue.isVisible = false
                    labelDeviceHostnameValue.isVisible = false
                    labelLocationsValue.isVisible = false
                    boardInfoPanel.isVisible = true
                    softInfoPanel.isVisible = true
                    BootloaderFastbootInfoPanel.isVisible = false
                    softFastbootInfoPanel.isVisible = false
                    StorageFastbootInfoPanel.isVisible = false
                    softInfoPanel.setBounds(10, 205, 290, 160)
                    deviceInfoPanel.setBounds(5, 5, 310, 376)
                    deviceControlPanel.setBounds(5, 385, 310, 85)
                    deviceConnection.setBounds(5, 475, 310, 100)
                    labelTCP.isVisible = true
                    labelTCPConnection.isVisible = true
                    buttonIpConnect.isVisible = true
                    textFieldIP.isVisible = true
                    labelConnect.isVisible = true
                    labelIP.isVisible = true
                    if (ConnectedViaAdb) {
                        buttonPowerOff.isEnabled = true
                        buttonReboot.isEnabled = true
                        buttonRecoveryReboot.isEnabled = true
                        buttonFastbootReboot.isEnabled = true
                    }else{
                        buttonReboot.isEnabled = false
                        buttonRecoveryReboot.isEnabled = false
                        buttonFastbootReboot.isEnabled = false
                        buttonPowerOff.isEnabled = false
                    }
                } else if (tabbedpane.selectedIndex == 2) {
                    boardInfoPanel.isVisible = false
                    softInfoPanel.isVisible = false
                    BootloaderFastbootInfoPanel.isVisible = true
                    softFastbootInfoPanel.isVisible = true
                    StorageFastbootInfoPanel.isVisible = true
                    softInfoPanel.setBounds(10, 205, 290, 165)
                    deviceInfoPanel.setBounds(5, 5, 310, 430)
                    deviceControlPanel.setBounds(5, 435, 310, 85)
                    deviceConnection.setBounds(5, 525, 310, 50)
                    labelTCP.isVisible = false
                    labelTCPConnection.isVisible = false
                    buttonIpConnect.isVisible = false
                    textFieldIP.isVisible = false
                    labelConnect.isVisible = false
                    labelIP.isVisible = false
                    if (ConnectedViaFastboot) {
                        buttonPowerOff.isEnabled = false
                        buttonReboot.isEnabled = true
                        buttonRecoveryReboot.isEnabled = true
                        buttonFastbootReboot.isEnabled = true
                    }else{
                        buttonReboot.isEnabled = false
                        buttonRecoveryReboot.isEnabled = false
                        buttonFastbootReboot.isEnabled = false
                        buttonPowerOff.isEnabled = false
                    }
                } else if (tabbedpane.selectedIndex == 3) {
                    labelGsmOperator.text = "USB mode:"
                    labelGsmOperator.bounds = Rectangle(15, 156, 70, 20)
                    labelGsmOperatorValue.bounds = Rectangle(90, 156, 175, 20)
                    labelCPU.text = "CPU vendor:"
                    labelCPU.bounds = Rectangle(15, 96, 80, 20)
                    labelVersionRelease.text = "Recovery version:"
                    labelVersionReleaseValue.bounds = Rectangle(125, 34, 160, 20)
                    labelCPUValue.bounds = Rectangle(100, 96, 280, 20)
                    labelLanguage.text = "Build ID:"
                    labelLanguageValue.setBounds(73, 93, 195, 20)
                    labelSecureBoot.isVisible = true
                    labelDeviceHostname.isVisible = true
                    labelLocations.isVisible = true
                    labelSecureBootValue.isVisible = true
                    labelDeviceHostnameValue.isVisible = true
                    labelLocationsValue.isVisible = true
                    boardInfoPanel.isVisible = true
                    softInfoPanel.isVisible = true
                    BootloaderFastbootInfoPanel.isVisible = false
                    softFastbootInfoPanel.isVisible = false
                    StorageFastbootInfoPanel.isVisible = false
                    softInfoPanel.setBounds(10, 205, 290, 215)
                    deviceInfoPanel.setBounds(5, 5, 310, 430)
                    deviceControlPanel.setBounds(5, 435, 310, 85)
                    deviceConnection.setBounds(5, 525, 310, 50)
                    labelTCP.isVisible = false
                    labelTCPConnection.isVisible = false
                    buttonIpConnect.isVisible = false
                    textFieldIP.isVisible = false
                    labelConnect.isVisible = false
                    labelIP.isVisible = false
                    if (ConnectedViaRecovery) {
                        buttonReboot.isEnabled = true
                        buttonRecoveryReboot.isEnabled = true
                        buttonFastbootReboot.isEnabled = true
                        buttonPowerOff.isEnabled = true
                    }else{
                        buttonReboot.isEnabled = false
                        buttonRecoveryReboot.isEnabled = false
                        buttonFastbootReboot.isEnabled = false
                        buttonPowerOff.isEnabled = false
                    }
                }
                else if (tabbedpane.selectedIndex == 4) {
                    boardInfoPanel.isVisible = true
                    softInfoPanel.isVisible = true
                    BootloaderFastbootInfoPanel.isVisible = false
                    softFastbootInfoPanel.isVisible = false
                    StorageFastbootInfoPanel.isVisible = false
                    deviceInfoPanel.setBounds(5, 5, 310, 380)
                    deviceControlPanel.setBounds(5, 385, 310, 85)
                    deviceConnection.setBounds(5, 475, 310, 100)
                    labelTCP.isVisible = true
                    labelTCPConnection.isVisible = true
                    buttonIpConnect.isVisible = true
                    textFieldIP.isVisible = true
                    labelConnect.isVisible = true
                    labelIP.isVisible = true
                    buttonReboot.isEnabled = false
                    buttonRecoveryReboot.isEnabled = false
                    buttonFastbootReboot.isEnabled = false
                    buttonPowerOff.isEnabled = false
                }
            }

            buttonInstallAll.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallAll.isEnabled = false
                        val paths: Array<File>?
                        val file = File(selectedDirectoryPath)
                        val fileNameFilter = FilenameFilter { _, name ->
                            if (name.lastIndexOf('.') > 0) {
                                val lastIndex = name.lastIndexOf('.')
                                val str = name.substring(lastIndex)
                                if (str == ".apk") {
                                    return@FilenameFilter true
                                }
                            }
                            false
                        }
                        paths = file.listFiles(fileNameFilter)
                        for (path in paths) {
                            if (Windows) {
                                exec("adb", "install \"$path\"")
                            } else {
                                exec("adb", "install $path")
                            }
                        }
                    }
                    override fun done() { buttonInstallAll.isEnabled = true }
                }
                Worker().execute()
            }


            buttonInstallOne.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallOne.isEnabled = false
                        if (Windows) {
                            exec("adb", "install \"$selectedFileAbsolutePath\"")
                        } else {
                            exec("adb", "install $selectedFileAbsolutePath")
                        }
                    }
                    override fun done() { buttonInstallOne.isEnabled = true }
                }
                Worker().execute()
            }


            buttonDisable.addActionListener {
                val textInput: String = if (textAreaInput.text != "You can enter app package here" && textAreaInput.text != "") {
                    textAreaInput.text
                } else {
                    list.selectedValue.toString()
                }

                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonDisable.isEnabled = false
                        exec("adb", "shell pm disable-user --user 0 $textInput")
                    }
                    override fun done() {
                        buttonDisable.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonUninstall.addActionListener {
                val textInput: String = if (textAreaInput.text != "You can enter app package here" && textAreaInput.text != "") {
                    textAreaInput.text
                } else {
                    list.selectedValue.toString()
                }

                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonUninstall.isEnabled = false
                        exec("adb", "shell pm uninstall --user 0 $textInput")
                    }

                    override fun done() {
                        buttonUninstall.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonEnable.addActionListener {
                val textInput: String = if (textAreaInput.text != "You can enter app package here" && textAreaInput.text != "") {
                    textAreaInput.text
                } else {
                    list.selectedValue.toString()
                }

                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonEnable.isEnabled = false
                        exec("adb", "shell pm enable $textInput")
                    }
                    override fun done() {
                        buttonEnable.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonCheck.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonCheck.isEnabled = false
                        textFieldIPa.isFocusable = true
                        when {
                            radioButtonDisabled.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val reader = execLines("adb shell pm list packages -d")
                                for(element in reader){
                                    if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element) {
                                        if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
                                            arrayList += element.substring(8)
                                        }
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                            }
                            radioButtonSystem.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val reader = execLines("adb shell pm list packages -s")
                                for(element in reader){
                                    if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element) {
                                        if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
                                            arrayList += element.substring(8)
                                        }
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                            }
                            radioButtonEnabled.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val reader = execLines("adb shell pm list packages -e")
                                for(element in reader){
                                    if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element) {
                                        if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
                                            arrayList += element.substring(8)
                                        }
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                            }
                            radioButtonThird.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val reader = execLines("adb shell pm list packages -3")
                                for(element in reader){
                                    if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element) {
                                        if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
                                            arrayList += element.substring(8)
                                        }
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                            }
                            else -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val reader = execLines("adb shell pm list packages")
                                for(element in reader){
                                    if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element) {
                                        if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
                                            arrayList += element.substring(8)
                                        }
                                    }
                                }
                                arrayList.sort()
                                buttonCheck.isEnabled = true
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                            }
                        }
                    }
                    override fun done() {
                        buttonCheck.isEnabled = true
                    }
                }
                Worker().execute()
            }

            buttonChooseOne.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChooseOne.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("APK Files", "apk")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Choose APK")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
                            selectedFilePath = choseFile.selectedFile.path
                            labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
                        }
                    }
                    override fun done() {
                        buttonChooseOne.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonChoseAll.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChoseAll.isEnabled = false
                        val choseDirectory = JFileChooser()
                        choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                        val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedDirectoryPath = choseDirectory.selectedFile.path
                            labelSelectedAll.text = "Selected: ${choseDirectory.selectedFile.path}"
                        }
                    }
                    override fun done() {
                        buttonChoseAll.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonRunCommand.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRunCommand.isEnabled = false
                        textAreaCommandOutput.text = exec("adb", textAreaCommandInput.text, output = true)
                    }
                    override fun done() {
                        buttonRunCommand.isEnabled = true
                    }
                }
                Worker().execute()
            }



            buttonRunCommandFastboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRunCommandFastboot.isEnabled = false
                        textAreaCommandFastbootOutput.text = exec("fastboot", textAreaCommandFastbootInput.text, output = true)
                    }

                    override fun done() {
                        buttonRunCommandFastboot.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonErase.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonErase.isEnabled = false
                        if (checkBoxPartitionBoot.isSelected) {
                            exec("fastboot", "erase boot")
                        }
                        if (checkBoxPartitionSystem.isSelected) {
                            exec("fastboot", "erase system")
                        }
                        if (checkBoxPartitionData.isSelected) {
                            exec("fastboot", "erase userdata")
                        }
                        if (checkBoxPartitionCache.isSelected) {
                            exec("fastboot", "erase cache")
                        }
                        if (checkBoxPartitionRecovery.isSelected) {
                            exec("fastboot", "erase recovery")
                        }
                        if (checkBoxPartitionRadio.isSelected) {
                            exec("fastboot", "erase radio")
                        }
                    }

                    override fun done() {
                        buttonErase.isEnabled = true
                    }
                }
                Worker().execute()
            }



            buttonChoseRecovery.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChoseRecovery.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("Recovery Files", "img")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
                            selectedFilePath = choseFile.selectedFile.path
                            labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
                        }
                    }
                    override fun done() {
                        buttonChoseRecovery.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonInstallRecovery.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallRecovery.isEnabled = false
                        if (Windows) {
                            exec("fastboot", "flash recovery \"$selectedFileAbsolutePath\"")
                        } else {
                            exec("fastboot", "flash recovery $selectedFileAbsolutePath")
                        }
                    }
                    override fun done() {
                        buttonInstallRecovery.isEnabled = true
                    }
                }
                Worker().execute()
            }


            buttonBootToRecovery.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonBootToRecovery.isEnabled = false
                        if (Windows) {
                            exec("fastboot", "boot \"$selectedFileAbsolutePath\"")
                        } else {
                            exec("fastboot", "boot $selectedFileAbsolutePath")
                        }
                    }

                    override fun done() {
                        buttonBootToRecovery.isEnabled = true
                    }
                }
                Worker().execute()
            }

            buttonChooseZip.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChooseZip.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("Zip files", "zip")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Select Zip")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedZipPath = choseFile.selectedFile.absolutePath
                        }
                    }
                    override fun done() {
                        buttonChooseZip.isEnabled = true
                    }
                }
                Worker().execute()
            }

            buttonInstallZip.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallZip.isEnabled = false
                        exec("adb", "shell twrp sideload")
                        Thread.sleep(3_000)
                        if (Windows) {
                            exec("adb", "sideload \"${selectedZipPath}\"")
                        } else {
                            exec("adb", "sideload $selectedZipPath")
                        }
                    }
                    override fun done() {
                        buttonInstallZip.isEnabled = true
                    }
                }
                Worker().execute()
            }
            boardInfoPanel.isVisible = true
            softInfoPanel.isVisible = true
            BootloaderFastbootInfoPanel.isVisible = false
            softFastbootInfoPanel.isVisible = false
            StorageFastbootInfoPanel.isVisible = false
            deviceInfoPanel.setBounds(5, 5, 310, 376)
            deviceControlPanel.setBounds(5, 385, 310, 85)
            deviceConnection.setBounds(5, 475, 310, 100)
            labelTCP.isVisible = true
            labelTCPConnection.isVisible = true
            buttonIpConnect.isVisible = true
            textFieldIP.isVisible = true
            labelConnect.isVisible = true
            labelIP.isVisible = true
            frame.isVisible = true
        }
    }
}
