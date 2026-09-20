package com.main.helper;
import android.util.Base64;
import com.main.models.Ingredient;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class IdGeneratorHelper {

    private static final String SECRET_KEY = "MySuperSecretKey";

    public static String generateID(Ingredient ingredient) throws Exception {

        String ingredientId = (ingredient.getName() + ingredient.getUnit() + ingredient.getCategory())
                .replaceAll("[^a-zA-Z0-9]", "");

        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(ingredientId.getBytes());

        // Use Base64 URL-safe encoding so Firestore doesn't reject characters like '/'
        return Base64.encodeToString(encryptedBytes, Base64.URL_SAFE | Base64.NO_WRAP);
    }


}
