package eu.virtualparadox.bandage.layout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Handles loading of the BandageLayout JNI native library.
 *
 * <ul>
 *   <li>First tries {@code System.loadLibrary("bandagelayout")}.</li>
 *   <li>If not found, detects OS/arch and extracts the correct native from JAR resources.</li>
 *   <li>Thread-safe and idempotent.</li>
 * </ul>
 */
public final class BandageLayoutInitializer {

    // --------------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------------

    private static final String LIB_NAME = "bandagelayout";

    private static final String OS_MAC = "mac";
    private static final String OS_LINUX = "linux";
    private static final String OS_WIN = "win";

    private static final String ARCH_ARM = "arm";
    private static final String ARCH_AARCH64 = "aarch64";

    private static final String OSKEY_MAC_ARM = "macos-arm64";
    private static final String OSKEY_MAC_X64 = "macos-x86_64";
    private static final String OSKEY_LINUX_ARM = "linux-aarch64";
    private static final String OSKEY_LINUX_X64 = "linux-x86_64";
    private static final String OSKEY_WIN_X64 = "windows-x86_64";

    private static final String NATIVES_BASE = "/native/";

    private static final String DLL_EXT = ".dll";
    private static final String DYLIB_EXT = ".dylib";
    private static final String SO_EXT = ".so";

    // --------------------------------------------------------------------------------------------
    // State
    // --------------------------------------------------------------------------------------------

    private static volatile boolean nativeLibLoaded = false;

    private BandageLayoutInitializer() {
        // prevent instantiation
    }

    /**
     * Initialize the BandageLayout JNI library exactly once.
     */
    public static synchronized void initialize() {
        if (nativeLibLoaded) {
            return;
        }

        // 1. Try system load
        try {
            System.loadLibrary(LIB_NAME);
            nativeLibLoaded = true;
            return;
        } catch (UnsatisfiedLinkError e) {
            // skip
        }

        // 2. Detect OS + arch
        final String osName = System.getProperty("os.name").toLowerCase();
        final String arch = System.getProperty("os.arch").toLowerCase();

        final String osKey;
        if (osName.contains(OS_MAC)) {
            osKey = (arch.contains(ARCH_ARM) || arch.contains(ARCH_AARCH64))
                    ? OSKEY_MAC_ARM
                    : OSKEY_MAC_X64;
        } else if (osName.contains(OS_LINUX)) {
            osKey = (arch.contains(ARCH_ARM) || arch.contains(ARCH_AARCH64))
                    ? OSKEY_LINUX_ARM
                    : OSKEY_LINUX_X64;
        } else if (osName.contains(OS_WIN)) {
            osKey = OSKEY_WIN_X64;
        } else {
            throw new UnsatisfiedLinkError("Unsupported OS/arch: " + osName + "/" + arch);
        }

        final String libResource;
        if (osKey.startsWith("windows")) {
            libResource = NATIVES_BASE + osKey + "/" + LIB_NAME + DLL_EXT;
        } else if (osKey.startsWith("macos")) {
            libResource = NATIVES_BASE + osKey + "/lib" + LIB_NAME + DYLIB_EXT;
        } else {
            libResource = NATIVES_BASE + osKey + "/lib" + LIB_NAME + SO_EXT;
        }

        // 3. Extract from JAR
        try (InputStream in = BandageLayoutInitializer.class.getResourceAsStream(libResource)) {
            if (in == null) {
                throw new UnsatisfiedLinkError("Native library not found in resources: " + libResource);
            }

            final File tmp = File.createTempFile(LIB_NAME, libResource.substring(libResource.lastIndexOf('.')));
            tmp.deleteOnExit();

            Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.load(tmp.getAbsolutePath());
            nativeLibLoaded = true;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library from JAR resources: " + libResource, e);
        }
    }

    /**
     * @return {@code true} if the library is already initialized.
     */
    public static boolean isInitialized() {
        return nativeLibLoaded;
    }
}
