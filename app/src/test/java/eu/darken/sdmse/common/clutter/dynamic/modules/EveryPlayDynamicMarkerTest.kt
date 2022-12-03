package eu.darken.sdmse.common.clutter.dynamic.modules

import eu.darken.sdmse.common.areas.DataArea
import eu.darken.sdmse.common.areas.DataArea.Type.SDCARD
import eu.darken.sdmse.common.pkgs.toPkgId
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EveryPlayDynamicMarkerTest {

    private val markerSource = EveryplayMarkerMatcher()

    @Test fun testMatching() = runTest {
        markerSource.match(SDCARD, ".EveryplayCache").size shouldBe 0

        markerSource.match(SDCARD, ".EveryplayCache/com.package.rollkuchen").single().apply {
            packageNames.single() shouldBe "com.package.rollkuchen".toPkgId()
        }
    }

    @Test fun testBadMatches() = runTest {
        markerSource.match(SDCARD, ".EveryplayCache/.nomedia").size shouldBe 0

        markerSource.match(SDCARD, ".EveryplayCache/images").size shouldBe 0

        markerSource.match(SDCARD, ".EveryplayCache/videos").size shouldBe 0
    }

    @Test fun testGetForLocation() = runTest {
        for (location in DataArea.Type.values()) {
            val markers = markerSource.getMarkerForLocation(location)
            if (location == SDCARD) {
                markers.single().apply {
                    prefixFreeBasePath shouldBe EVERYPLAY_CACHE
                    isPrefixFreeBasePathDirect shouldBe false
                }
            } else {
                markers.isEmpty() shouldBe true
            }
        }
    }

    @Test fun testGetForPackageName() = runTest {
        val testPkg = "com.pkg.test".toPkgId()
        markerSource.getMarkerForPkg(testPkg).single().apply {
            prefixFreeBasePath shouldBe "$EVERYPLAY_CACHE/$testPkg"
            match(SDCARD, "$EVERYPLAY_CACHE/$testPkg/something") shouldBe null
            match(SDCARD, "$EVERYPLAY_CACHE/$testPkg") shouldNotBe null
            isPrefixFreeBasePathDirect shouldBe true
        }
    }

    companion object {
        private const val EVERYPLAY_CACHE = ".EveryplayCache"
    }
}