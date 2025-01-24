package com.example.blinkshop

import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class accessToken {

    fun getAccessToken(): String? {
        var firebaseMessagingScope ="https://www.googleapis.com/auth/firebase.messaging"
        try {
            val jsonString: String =

                "{\n" +
                        "\"type\": \"service_account\",\n" +
                        "\"project_id\": \"shopping-cart-80cc4\",\n" +
                        "\"private_key_id\": \"7eeb899720e023aaac15b8f6ecc5d3723aa65ae5\",\n"+
                        "\"private_key\": \"-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCTzeVick31CENE\nRR7Q5kTyv5OkNnkScDoC0hsm3JHIFdr9GcOZtNlSLFi3+nTHgWLOq9+0hz170tii\nSNZCJatKb+DKv8AZl/DQNr79gik5x1r7o9KossvXQXuum/Ya0YQf2wmCzFwZlNR8\n8BxKw/dq6qJg8aej8nsMsCDFT8YITNBRmXwkzqcBq53sFvuBtnHXMBtg/ZkcTNGF\nPsENhq09Ub9NxjfePzcOivqe1kKCBjewV8yMJgiC8pzylTjFRPql9oNurgRhTxJc\n1PoZtDlRMNZBiQN4sZIZ94phXzUKrHgFKUp4Nbt5LsHnr4whtxbaBzTpfIOcA9Yq\nv+QtCkmTAgMBAAECggEADvewx6a6TPAImnyYFvYv++0j7w3dIN2LN7Hoh1JFfl+d\njOKeXjhkMYrFc+FsZgUCPoTv4yuMMbfeXEDIAcF9dneclKuVBoJ5I9IHcUtMZXTS\nD92SfabtjDTYvjaTq4+VXlcjasLLKNlbffN1dKoiVLTCENMn84BLWbOUU1GKxQLq\n7SLPtJeHQ+1ZNhKtznbteV3FDUYJxbCIJBlWf3xDIivnGDUCbPe+CSF2FDVrIe0Z\nZti3V5N7vwaIvaTyF6GeH+wFhDhjuXkxzcra1ohGdsYNNevn5c9xdKmL9O4IFFSW\ncSyckKWviNZAgQKGtFu6HhqkdvHpUzuF9kOpBhhUsQKBgQDJERFj5PTWGK+wCsya\n+/6xrK2H2hkpY1vWzvfC/o3EXS//S5Tiv2TCfHDQjKEh3Xszq5YTSp1MFkGsmfga\n7DJPLWk5NHWoBFSUrNOWq9JOjNIRj317rf9XpUZ6UrRD+SUVW5iOOde4KeusE8YN\nWyCWKSQJzkkQX74+SLBZiT5TRQKBgQC8L5HB0PFZ+F04JMXXxgElS2380XowrZAx\nILwawE3m54YR1O3UfcoPuz21TwtqkzOiRnSyosJW9NhYJ1Qr1zuKn7yuEn408j+d\n8soZ2r7hKQ2VnG0uFKDsnDq/HI3/juSZzlmCDUQpX9tfm+OknvF+BeZi+gn108GS\n+sMq6N5K9wKBgQCUj6bgv1/j/PN6yE/rR7wlPWguU4h52y8VZXENQ5Yt+4imQgst\nCquBQ/77KJ0573LDd8l4UY4tlNhLlqwKAHEfrXbszIUOcryn6Zag4i2O2l+wl+vo\ngWSENtr/MLJ+8sxinn32wGhSa671Qtr4LVfDe3xkPmYf0g436O9cCJrjXQKBgDG1\n3yrzps29beKG1DxLccIISuWd8EkrkAtqOkZWxj1Yq3HKbYjUMaw2KfbbCoj8mYck\nVW9azztpMj0XJoTuNixIRe81s2EJHQT8PabWwdV4or4CtFSX3mqiWmJhiKEZfgmj\nGMHh/z9usuRuqZOy6NgBdvpuUnM9nMTWuPd8zDvBAoGBAI65uZ4EW1dqZsXq0Gm9\nWYXQy0Qo/B+nInqWY9AxtJy9RdzwsERsPalHvd0QRXCi4n0fKPpjyMAnO09F10/Y\nQb0IF9sw0q1/MKu9ky1U+ExSJQXD+f15733cwD4XusuDvD1jw+9ZuG0S4N1plM/C\nuR5ItudmtN85X9DQKVcCmQfh\n-----END PRIVATE KEY-----\n\",\n"+
                        "\"client_email\":\"shopping-cart-80cc4@appspot.gserviceaccount.com\",\n"+
                        "\"client_id\": \"100988757194429273243\",\n"+
                        "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"+
                        "\"token_uri\": \"https://oauth2.googleapis.com/token\",\n"+
                        "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"+
                        "\"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/shopping-cart-80cc4%40appspot.gserviceaccount.com\",\n"+
                        "\"universe_domain\": \"googleapis.com\"\n"+
                        "}"

            val stream:InputStream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))
            val credentials:GoogleCredentials = GoogleCredentials.fromStream(stream).createScoped((arrayListOf(firebaseMessagingScope)))
            credentials.refresh()
           // val accessToken: AccessToken = credentials.refreshAccessToken()
            return credentials.accessToken.tokenValue
        }catch (e:IOException){
            e.printStackTrace()
            throw RuntimeException("Failed to obtain access token", e)
            return null
        }
    }
}