package fr.teampeps.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {

    public static byte[] compressImage(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }

        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[4096];

            while (!deflater.finished()) {
                int size = deflater.deflate(buffer);
                outputStream.write(buffer, 0, size);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while compressing image", e);
        } finally {
            deflater.end();
        }
    }


    public static byte[] decompressImage(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }

        Inflater inflater = new Inflater();
        inflater.setInput(data);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[4096];

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            return outputStream.toByteArray();
        } catch (DataFormatException | IOException e) {
            throw new RuntimeException("Error while decompressing image", e);
        } finally {
            inflater.end();
        }
    }

}