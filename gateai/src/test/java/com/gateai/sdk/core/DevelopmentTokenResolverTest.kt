package com.gateai.sdk.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DevelopmentTokenResolverTest {
    @Test fun `non-debuggable apps ignore explicit tokens without reading resources`() {
        assertNull(DevelopmentTokenResolver.resolve(false, "explicit-secret") {
            error("Release apps must not read token resources")
        })
    }

    @Test fun `non-debuggable apps never discover resource tokens`() {
        assertNull(DevelopmentTokenResolver.resolve(false, null) {
            error("Release apps must not read token resources")
        })
    }

    @Test fun `debug apps discover the resource when no token is configured`() {
        assertEquals("resource-secret", DevelopmentTokenResolver.resolve(true, null) { "resource-secret" })
    }

    @Test fun `explicit debug tokens take precedence without reading resources`() {
        assertEquals("explicit-secret", DevelopmentTokenResolver.resolve(true, "explicit-secret") {
            error("Explicit configuration must take precedence")
        })
    }

    @Test fun `blank explicit tokens fall back to resources`() {
        for (token in listOf("", "  ", "\n")) {
            assertEquals("resource-secret", DevelopmentTokenResolver.resolve(true, token) { "resource-secret" })
        }
    }

    @Test fun `absent and blank resources leave attestation enabled`() {
        for (token in listOf(null, "", "  ", "\n")) {
            assertNull(DevelopmentTokenResolver.resolve(true, null) { token })
        }
    }
}
