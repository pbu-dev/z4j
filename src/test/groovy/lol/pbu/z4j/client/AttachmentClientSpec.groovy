/*
 * Copyright 2026 Peanut Butter Unicorn, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lol.pbu.z4j.client

import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import lol.pbu.z4j.Z4jSpec
import lol.pbu.z4j.model.AttachmentResponse
import lol.pbu.z4j.model.AttachmentUploadResponse
import spock.lang.Shared

import java.nio.charset.StandardCharsets

@MicronautTest
class AttachmentClientSpec extends Z4jSpec {

    @Shared AttachmentClient adminAttachmentClient, agentAttachmentClient, badUrlAttachmentClient
    @Shared Long uploadedAttachmentId
    @Shared String uploadToken

    def setupSpec() {
        adminAttachmentClient = adminCtx.getBean(AttachmentClient.class)
        agentAttachmentClient = agentCtx.getBean(AttachmentClient.class)
        badUrlAttachmentClient = badUrlCtx.getBean(AttachmentClient.class)

        String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        byte[] content = "Hello attachment test ${entropy}".getBytes(StandardCharsets.UTF_8)
        AttachmentUploadResponse response = agentAttachmentClient.uploadAttachment(
                "z4j-test-${entropy}.txt",
                "text/plain",
                content
        ).block()

        uploadToken = response?.upload?.token
        uploadedAttachmentId = response?.upload?.attachment?.id
    }

    def "can upload attachment as agent"() {
        given: "valid PNG image binary and unique filename"
        String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        byte[] content = [
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D,
                0x49, 0x48, 0x44, 0x52, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4, (byte) 0x89, 0x00, 0x00,
                0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00,
                0x00, 0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00, 0x00, 0x00, 0x00,
                0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        ] as byte[]
        String filename = "test-image-${entropy}.png"

        when: "uploading attachment"
        AttachmentUploadResponse response = agentAttachmentClient.uploadAttachment(
                filename,
                "image/png",
                content
        ).block()

        then: "response contains upload token and attachment metadata"
        noExceptionThrown()
        response != null
        response.upload != null
        response.upload.token != null
        response.upload.attachment != null
        response.upload.attachment.id != null
        response.upload.attachment.fileName == filename
        response.upload.attachment.contentType != null
        response.upload.attachment.size == (long) content.length
    }

    def "can upload additional attachment reusing existing token"() {
        given: "an existing token and second file content"
        String entropy = UUID.randomUUID().toString().replace("-", "").substring(0, 8)
        byte[] content = "SECOND_FILE_${entropy}".getBytes(StandardCharsets.UTF_8)
        String filename = "second-${entropy}.txt"

        when: "uploading with token"
        AttachmentUploadResponse response = agentAttachmentClient.uploadAttachment(
                filename,
                uploadToken,
                "text/plain",
                content
        ).block()

        then: "attachment is added and token is preserved"
        noExceptionThrown()
        response != null
        response.upload != null
        response.upload.token == uploadToken
        response.upload.attachment != null
        response.upload.attachment.fileName == filename
    }

    def "can show attachment details by id"() {
        when: "fetching attachment by id"
        AttachmentResponse response = agentAttachmentClient.showAttachment(uploadedAttachmentId).block()

        then: "attachment metadata is returned"
        noExceptionThrown()
        response != null
        response.attachment != null
        response.attachment.id == uploadedAttachmentId
    }

    def "uploading attachment fails with bad url client"() {
        when: "calling upload on bad url client"
        badUrlAttachmentClient.uploadAttachment("test.txt", "text/plain", "data".bytes).block()

        then: "HttpClientException is thrown"
        thrown(HttpClientException)
    }
}
