package br.com.gestaopsicologica.security.criptografia;

import br.com.gestaopsicologica.exceptions.CriptografiaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class AesGcmCriptografiaService implements CriptografiaService {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final String VERSION_PREFIX = "v1:";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKeySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmCriptografiaService(@Value("${app.crypto.evolucao-key}") String chaveBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(chaveBase64);

        if (keyBytes.length != 32) {
            throw new CriptografiaException("A chave de criptografia precisa ter 32 bytes para AES-256.");
        }

        this.secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
    }


    @Override
    public String encriptografa(String texto) {
        if (texto == null) {
            return null;
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

            byte[] textoCriptografado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[iv.length + textoCriptografado.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(textoCriptografado, 0, payload, iv.length, textoCriptografado.length);

            return VERSION_PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao criptografar conteúdo da evolução.", e);
        }
    }

    @Override
    public String descriptografa(String texto) {
        if (texto == null) {
            return null;
        }

        try {
            if (!texto.startsWith(VERSION_PREFIX)) {
                throw new CriptografiaException("Formato de payload criptografado inválido.");
            }

            String conteudoBase64 = texto.substring(VERSION_PREFIX.length());
            byte[] payload = Base64.getDecoder().decode(conteudoBase64);

            if (payload.length <= IV_LENGTH) {
                throw new CriptografiaException("Payload criptografado inválido");
            }

            byte[] iv = Arrays.copyOfRange(payload, 0, IV_LENGTH);
            byte[] textoCriptografado = Arrays.copyOfRange(payload, IV_LENGTH, payload.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

            byte[] textoPlano = cipher.doFinal(textoCriptografado);
            return new String(textoPlano, StandardCharsets.UTF_8);
        } catch (CriptografiaException e) {
            throw e;
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao descriptografar conteúdo da evolução.", e);
        }
    }
}
