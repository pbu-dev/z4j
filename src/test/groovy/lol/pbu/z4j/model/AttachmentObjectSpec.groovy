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
package lol.pbu.z4j.model

import lol.pbu.z4j.Z4jSpec

class AttachmentObjectSpec extends Z4jSpec {

    def "should instantiate and set properties on AttachmentObject"() {
        given:
        def attachment = new AttachmentObject()

        when:
        attachment.setId(101L)
                  .setFileName("photo.png")
                  .setContentUrl("https://example.com/photo.png")
                  .setContentType("image/png")
                  .setSize(1024L)
                  .setWidth(640L)
                  .setHeight(480L)
                  .setInline(true)
                  .setDeleted(false)
                  .setUrl("https://example.zendesk.com/api/v2/attachments/101.json")
                  .setMalwareScanResult("malware_not_found")
                  .setThumbnails([])

        then:
        attachment.id == 101L
        attachment.fileName == "photo.png"
        attachment.contentUrl == "https://example.com/photo.png"
        attachment.contentType == "image/png"
        attachment.size == 1024L
        attachment.width == 640L
        attachment.height == 480L
        attachment.inline == true
        attachment.deleted == false
        attachment.url == "https://example.zendesk.com/api/v2/attachments/101.json"
        attachment.malwareScanResult == "malware_not_found"
        attachment.thumbnails == []
    }

    def "should instantiate AttachmentUploadResponse and inner upload"() {
        given:
        def att = new AttachmentObject().setId(202L).setFileName("doc.pdf")
        def upload = new AttachmentUploadResponseUpload()
                .setToken("tok_123")
                .setAttachment(att)
                .addAttachmentsItem(att)

        when:
        def response = new AttachmentUploadResponse(upload)

        then:
        response.upload != null
        response.upload.token == "tok_123"
        response.upload.attachment.fileName == "doc.pdf"
        response.upload.attachments.size() == 1
    }

    def "should instantiate AttachmentResponse"() {
        given:
        def att = new AttachmentObject().setId(303L)

        when:
        def response = new AttachmentResponse(att)

        then:
        response.attachment != null
        response.attachment.id == 303L
    }
}
