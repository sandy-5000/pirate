package com.darkube.pirate.services

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey

object KeyStoreManager {
    private const val KEY_ALIAS = "PirateChatRSA"

    private fun generateKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(2048)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.generateKeyPair()
    }

    fun getPrivateKey(): PrivateKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as PrivateKey
    }

    fun getPublicKey(): String? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val publicKey = keyStore.getCertificate(KEY_ALIAS)?.publicKey
        return Base64.encodeToString(publicKey?.encoded, Base64.DEFAULT)
    }

    private fun deleteKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.deleteEntry(KEY_ALIAS)
        }
        Thread.sleep(200)
    }

    fun regenerateKeyPair() {
        deleteKey()
        generateKeyPair()
    }
}
