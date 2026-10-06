package com.gateai.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RealtimeCallRequestTest {

    @Test
    fun `multipart body carries the offer and session with the boundary`() {
        val body = RealtimeCallRequest.multipartBody(
            offerSdp = "v=0\r\no=- 1 2 IN IP4 127.0.0.1\r\n",
            sessionJson = """{"type":"realtime","model":"gpt-realtime"}""",
            boundary = "b"
        ).toString(Charsets.UTF_8)

        assertTrue(body.startsWith("--b\r\nContent-Disposition: form-data; name=\"sdp\"\r\nContent-Type: application/sdp\r\n\r\nv=0\r\n"))
        assertTrue(body.contains("\r\n--b\r\nContent-Disposition: form-data; name=\"session\"\r\nContent-Type: application/json\r\n\r\n{\"type\":\"realtime\",\"model\":\"gpt-realtime\"}\r\n"))
        assertTrue(body.endsWith("\r\n--b--\r\n"))
    }

    @Test
    fun `call id is read from the Location header`() {
        assertEquals("rtc_abc123", RealtimeCallRequest.callIdFromLocation("/v1/realtime/calls/rtc_abc123"))
        assertEquals("rtc_x", RealtimeCallRequest.callIdFromLocation("https://api.openai.com/v1/realtime/calls/rtc_x?y=1"))
        assertNull(RealtimeCallRequest.callIdFromLocation("/v1/realtime/calls/"))
        assertNull(RealtimeCallRequest.callIdFromLocation(null))
    }
}
