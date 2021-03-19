package com.github.gradle.node.util

import spock.lang.Specification
import spock.lang.Unroll

class PlatformHelperTest extends Specification {
    private Properties props
    private PlatformHelper helper

    def setup() {
        this.props = new Properties()
        PlatformHelper.INSTANCE = this.helper = new PlatformHelper(this.props)
    }

    @Unroll
    def "check os and architecture for #osProp (#archProp)"() {
        given:
        this.props.setProperty("os.name", osProp)
        this.props.setProperty("os.arch", archProp)

        expect:
        this.helper.getOsName() == osName
        this.helper.getOsArch() == osArch
        this.helper.isWindows() == isWindows

        where:
        osProp      | archProp | osName   | osArch | isWindows
        'Windows 8' | 'x86'    | 'win'    | 'x86'  | true
        'Windows 8' | 'x86_64' | 'win'    | 'x64'  | true
        'Mac OS X'  | 'x86'    | 'darwin' | 'x86'  | false
        'Mac OS X'  | 'x86_64' | 'darwin' | 'x64'  | false
        'Linux'     | 'x86'    | 'linux'  | 'x86'  | false
        'Linux'     | 'x86_64' | 'linux'  | 'x64'  | false
        'SunOS'     | 'x86'    | 'sunos'  | 'x86'  | false
        'SunOS'     | 'x86_64' | 'sunos'  | 'x64'  | false
    }

    @Unroll
    def "verify ARM handling #archProp (#unameProp)"() {
        given:
        this.props.setProperty("os.name", osProp)
        this.props.setProperty("os.arch", archProp)
        this.props.setProperty("uname", unameProp)

        expect:
        this.helper.getOsName() == osName
        this.helper.getOsArch() == osArch

        where:
        osProp     | archProp  | unameProp | osName   | osArch
        'Linux'    | 'arm'     | 'armv7l'  | 'linux'  | 'armv7l' // Raspberry Pi 3
        'Linux'    | 'arm'     | 'armv8l'  | 'linux'  | 'arm64'
        'Linux'    | 'aarch32' | 'arm'     | 'linux'  | 'arm'
        'Linux'    | 'aarch64' | 'arm64'   | 'linux'  | 'arm64'
        'Linux'    | 'aarch64' | 'aarch64' | 'linux'  | 'arm64'
        // Apple Silicon, we use the x64 emulation until a Node.js bundle is provided for the platform
        'Mac OS X' | 'aarch64' | 'arm64'   | 'darwin' | 'x64'
    }

    def "throw exception if unsupported os"() {
        given:
        this.props.setProperty("os.name", 'Nonsense')

        when:
        this.helper.getOsName()

        then:
        thrown(IllegalStateException)
    }
}
